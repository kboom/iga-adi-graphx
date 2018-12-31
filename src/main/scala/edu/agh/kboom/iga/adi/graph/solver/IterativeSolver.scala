package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.SolverConfig.LoadedSolverConfig
import edu.agh.kboom.iga.adi.graph.solver.core._
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix

import scala.annotation.tailrec

object IterativeSolver {

  def noCoefficients(i: Int, j: Int): Double = 0

}

case class StepInformation(step: Int) {
  def nextStep(): StepInformation = StepInformation(step + 1)
}

case class IterativeSolver(stepSolver: StepSolver) {

  val LoggingConfig = LoadedSolverConfig.logging
  val OutputConfig = LoadedSolverConfig.output

  private val mesh: Mesh = stepSolver.directionSolver.mesh

  def solve(initialProblem: StaticProblem, nextProblem: (SplineSurface, StepInformation) => Option[Problem])(implicit sc: SparkContext): Unit = {
    solveAll(initialProblem, PlainSurface(mesh), StepInformation(0), nextProblem)
  }

  @tailrec
  private def solveAll(problem: Problem, surface: Surface, stepInformation: StepInformation, nextProblem: (SplineSurface, StepInformation) => Option[Problem])(implicit sc: SparkContext): StepInformation = {
    if (LoggingConfig.operations) {
      println(f"Iteration ${stepInformation.step}")
    }
    val ctx = IgaContext(mesh, problem)
    val nextProjection = stepSolver.solve(ctx)(surface)

    processSolution(nextProjection, stepInformation)

    val nextStep = stepInformation.nextStep()
    nextProblem(nextProjection, nextStep) match {
      case Some(next) => solveAll(next, nextProjection, nextStep, nextProblem)
      case None => stepInformation
    }
  }

  private def processSolution(projection: SplineSurface, stepInformation: StepInformation)(implicit sc: SparkContext) = {
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
