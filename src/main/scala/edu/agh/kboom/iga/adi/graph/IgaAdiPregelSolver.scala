package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.problems.{HeatTransferProblem, LinearProblem}
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

    iterativeSolver.solve(LinearProblem, (_, step) => step match {
      case StepInformation(step) if step < problemConfig.steps => Some(HeatTransferProblem(mesh))
      case _ => None
    })

    spark.stop()
  }

}
