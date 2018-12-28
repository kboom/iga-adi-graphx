package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.solver.{DirectionSolver, IgaContext, IterativeSolver, StepSolver}
import edu.agh.kboom.iga.adi.graph.solver.core._
import org.apache.spark.sql.SparkSession

object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    implicit val sc = spark.sparkContext

    // OK: 12, 48
    val mesh = Mesh(24, 24, 24, 24)
    val iterativeSolver = new IterativeSolver(StepSolver(DirectionSolver(mesh)))

    iterativeSolver.solve(LinearProblem, _ => None)

    spark.stop()
  }


}
