package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.core.{Mesh, Problem, Projection, StaticProblem}
import org.apache.spark.SparkContext

import scala.annotation.tailrec

object IterativeSolver {

  def noCoefficients(i: Int, j: Int): Double = 0

}

case class IterativeSolver(stepSolver: StepSolver) {

  private val mesh: Mesh = stepSolver.directionSolver.mesh

  def solve(initialProblem: StaticProblem, nextProblem: (Projection) => Option[Problem])(implicit sc: SparkContext): Unit = {
    val initialProjection = ProjectionLoader.loadSurface(mesh, initialProblem)
    solveAll(initialProblem, initialProjection, nextProblem)
  }

  @tailrec
  private def solveAll(problem: Problem, projection: Projection, nextProblem: (Projection) => Option[Problem], step: Int = 0)(implicit sc: SparkContext): Int = {
    println(f"Iteration $step")
    val ctx = IgaContext(mesh, problem)
    val nextProjection = stepSolver.solve(ctx)(projection)
    nextProblem(nextProjection) match {
      case Some(next) => solveAll(next, nextProjection, nextProblem, step + 1)
      case None => 1
    }
  }

}
