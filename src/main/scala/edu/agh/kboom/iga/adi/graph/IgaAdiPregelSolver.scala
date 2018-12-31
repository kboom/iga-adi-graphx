package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.problems.{HeatTransferProblem, LinearProblem, OneProblem, ProblemFactory}
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver._
import org.apache.spark.sql.SparkSession

object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    implicit val sc = spark.sparkContext

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

    spark.stop()
  }

}
