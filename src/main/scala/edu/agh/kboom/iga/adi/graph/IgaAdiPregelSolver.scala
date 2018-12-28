package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.core._
import org.apache.spark.sql.SparkSession

object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    implicit val sc = spark.sparkContext

    // OK: 12, 48
    val mesh = Mesh(24, 24, 24, 24)
    val stepSolver = StepSolver(DirectionSolver(mesh))

    stepSolver.solve(IgaContext(mesh, LinearProblem.valueAt))

    spark.stop()
  }


}
