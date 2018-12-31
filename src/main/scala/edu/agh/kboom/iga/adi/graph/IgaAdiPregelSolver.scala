package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.problems.{HeatTransferProblem, ProblemFactory}
import edu.agh.kboom.iga.adi.graph.solver._
import edu.agh.kboom.iga.adi.graph.solver.core._
import org.apache.spark.{SparkConf, SparkContext}

object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    implicit val sc = new SparkContext(new SparkConf())

    val problemConfig = SolverConfig.LoadedSolverConfig.problem
    val problemSize = problemConfig.size
    val mesh = Mesh(problemSize, problemSize, problemSize, problemSize)

    val iterativeSolver = IterativeSolver(StepSolver(DirectionSolver(mesh)))

    val initialProblem = ProblemFactory.initialProblem(problemConfig)

    val steps = iterativeSolver.solve(initialProblem, (_, step) => step match {
      case StepInformation(step, _) if step < problemConfig.steps => Some(HeatTransferProblem(mesh))
      case _ => None
    })

    val totalInSeconds = (System.currentTimeMillis() - steps.head.timer.start) / 1000

    println(f"Total time (s): ${totalInSeconds}")
  }

}
