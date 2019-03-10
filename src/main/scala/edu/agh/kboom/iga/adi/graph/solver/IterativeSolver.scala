package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.TimeEventType.{STEP_FINISHED, STEP_STARTED}
import edu.agh.kboom.iga.adi.graph.TimeRecorder
import edu.agh.kboom.iga.adi.graph.solver.SolverConfig.LoadedSolverConfig
import edu.agh.kboom.iga.adi.graph.solver.core._
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix
import org.slf4j.LoggerFactory

import scala.annotation.tailrec

object IterativeSolver {

  private val Log = LoggerFactory.getLogger(classOf[IterativeSolver])

}

case class StepInformation(step: Int, timeRecorder: TimeRecorder = TimeRecorder.empty()) {
  def nextStep(): StepInformation = StepInformation(step + 1)
}

case class IterativeSolver(stepSolver: StepSolver) {

  val LoggingConfig = LoadedSolverConfig.logging
  val OutputConfig = LoadedSolverConfig.output

  private val mesh: Mesh = stepSolver.directionSolver.mesh

  def solve(initialProblem: StaticProblem, nextProblem: (SplineSurface, StepInformation) => Option[Problem])(implicit sc: SparkContext): Seq[StepInformation] =
    solveAll(initialProblem, PlainSurface(mesh), Seq(), nextProblem)


  @tailrec
  private def solveAll(problem: Problem, surface: Surface, historicSteps: Seq[StepInformation], nextProblem: (SplineSurface, StepInformation) => Option[Problem])(implicit sc: SparkContext): Seq[StepInformation] = {
    val step = historicSteps.lastOption.map(_.nextStep()).getOrElse(StepInformation(0))

    val recorder = step.timeRecorder

    recorder.record(STEP_STARTED)
    IterativeSolver.Log.info(f"Iteration ${step.step}, start time: ${recorder.times.head.startTimestamp()}")

    val ctx = IgaContext(mesh, problem, HORIZONTAL)
    val nextProjection = stepSolver.solve(ctx, recorder)(surface)

    recorder.record(STEP_FINISHED)

    surface match {
      case SplineSurface(rows, _) => rows.unpersist(blocking = false)
      case _ => Unit
    }

    processSolution(nextProjection, step)

    nextProblem(nextProjection, step) match {
      case Some(next) => solveAll(next, nextProjection, historicSteps ++ Seq(step), nextProblem)
      case None => Seq(step)
    }
  }

  private def processSolution(projection: SplineSurface, stepInformation: StepInformation)(implicit sc: SparkContext): Unit = {
    val surface = SplineSurface.surface(projection)

    if (LoggingConfig.surfaces) {
      printSurface(stepInformation, surface)
    }

    if (OutputConfig.store) {
      val filename = LoadedSolverConfig.output.filenameFor(stepInformation)
      surface.rows.map(row => (row.index, row.vector.toArray.mkString("[", ",", "]"))).saveAsTextFile(filename)
    }
  }

  private def printSurface(stepInformation: StepInformation, surface: IndexedRowMatrix) = {
    val stringMatrix = surface.rows.sortBy(_.index).map(_.vector.toArray.map(s => f"$s%.2f").mkString("\t")).collect().mkString(System.lineSeparator())
    println(f"\nSurface ${stepInformation.step}\n${stringMatrix}\n")
  }
}
