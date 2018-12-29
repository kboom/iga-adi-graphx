package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.core.{Mesh, Problem, Projection, StaticProblem}
import org.apache.spark.SparkContext

import scala.annotation.tailrec

object IterativeSolver {

  def noCoefficients(i: Int, j: Int): Double = 0

}

case class StepInformation(step: Int) {
  def nextStep(): StepInformation = StepInformation(step + 1)
}

case class IterativeSolver(stepSolver: StepSolver) {

  private val mesh: Mesh = stepSolver.directionSolver.mesh

  def solve(initialProblem: StaticProblem, nextProblem: (Projection, StepInformation) => Option[Problem])(implicit sc: SparkContext): Unit = {
    val initialProjection = ProjectionLoader.loadSurface(mesh, initialProblem)
    solveAll(initialProblem, initialProjection, StepInformation(0), nextProblem)
  }

  @tailrec
  private def solveAll(problem: Problem, projection: Projection, stepInformation: StepInformation, nextProblem: (Projection, StepInformation) => Option[Problem])(implicit sc: SparkContext): StepInformation = {
    println(f"Iteration ${stepInformation.step}")
    val ctx = IgaContext(mesh, problem)
    val nextProjection = stepSolver.solve(ctx)(projection)
    val nextStep = stepInformation.nextStep()
    nextProblem(nextProjection, nextStep) match {
      case Some(next) => solveAll(next, nextProjection, nextStep, nextProblem)
      case None => stepInformation
    }
  }

}
