package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.core.{Problem, Solution}
import org.apache.spark.SparkContext

import scala.annotation.tailrec

case class IterativeSolver(stepSolver: StepSolver) {

  def solve(initialProblem: Problem, nextProblem: (Solution) => Option[Problem])(implicit sc: SparkContext): Unit = {
    solveAll(initialProblem, nextProblem)
  }

  @tailrec
  private def solveAll(problem: Problem, nextProblem: (Solution) => Option[Problem], step: Int = 0)(implicit sc: SparkContext): Int = {
    println(f"Iteration $step")
    val ctx = IgaContext(stepSolver.directionSolver.mesh, problem.valueAt)
    val solution = stepSolver.solve(ctx)
    nextProblem(solution) match {
      case Some(next) => solveAll(next, nextProblem, step + 1)
      case None => 1
    }
  }

}
