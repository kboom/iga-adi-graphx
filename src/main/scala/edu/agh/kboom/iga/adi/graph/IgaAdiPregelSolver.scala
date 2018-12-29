package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.problems.{HeatTransferProblem, LinearProblem}
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.{DirectionSolver, IterativeSolver, StepSolver}
import org.apache.spark.sql.SparkSession

object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    implicit val sc = spark.sparkContext

    // OK: 12, 48
    val mesh = Mesh(12, 12, 12, 12)
    val iterativeSolver = IterativeSolver(StepSolver(DirectionSolver(mesh)))

    iterativeSolver.solve(LinearProblem, _ => Some(HeatTransferProblem(mesh)))

    spark.stop()
  }

}
