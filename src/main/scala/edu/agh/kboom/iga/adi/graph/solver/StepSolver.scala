package edu.agh.kboom.iga.adi.graph.solver

import breeze.linalg.DenseVector
import edu.agh.kboom.iga.adi.graph.TimeEventType._
import edu.agh.kboom.iga.adi.graph.TimeRecorder
import edu.agh.kboom.iga.adi.graph.solver.StepSolver.transposeRowMatrix
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.{HorizontalInitializer, VerticalInitializer}
import edu.agh.kboom.iga.adi.graph.solver.core.{SplineSurface, Surface}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}
import org.apache.spark.rdd.RDD

case class StepSolver(directionSolver: DirectionSolver) {

  val loggingConfig = SolverConfig.LoadedSolverConfig.logging

  def solve(ctx: IgaContext, rec: TimeRecorder = TimeRecorder.empty())(surface: Surface)(implicit sc: SparkContext): SplineSurface = {
    rec.record(HORIZONTAL_STARTED)
    val partialSolution = directionSolver.solve(ctx, HorizontalInitializer(surface, ctx.problem), rec)

    rec.record(TRANSPOSITION_STARTED)

    val transposedMatrix = transposeRowMatrix(partialSolution.m)

    val transposedPartialSolution = SplineSurface(transposedMatrix, ctx.mesh)

    if (loggingConfig.elements) {
      SplineSurface.print(transposedPartialSolution)
    }

    rec.record(VERTICAL_STARTED)

    val newProjection = directionSolver.solve(ctx.changedDirection(), VerticalInitializer(transposedPartialSolution), rec)

    if (loggingConfig.elements) {
      SplineSurface.print(newProjection)
    }

    transposedPartialSolution.m.unpersist(blocking = false)

    newProjection
  }

}

object StepSolver {

  def transposeRowMatrix(m: RDD[(Long, DenseVector[Double])]): RDD[(Long, DenseVector[Double])] = {
    val transposedRowsRDD = m.mapPartitions(rowToTransposedTriplet)
      .mapPartitions(_.flatten) // now we have triplets (newRowIndex, (newColIndex, value))
      .groupByKey
      .mapPartitions(
        _.map { case (a, b) => buildRow(a, b) },
        preservesPartitioning = true
      )
      .cache()

    if (!transposedRowsRDD.isEmpty()) {
      // trigger operation
    }

    transposedRowsRDD
  }

  def rowToTransposedTriplet(i: Iterator[(Long, DenseVector[Double])]): Iterator[Array[(Long, (Long, Double))]] =
    i.map(row => row._2.data.zipWithIndex.map {
      case (value, colIndex) => (colIndex.toLong, (row._1, value))
    })

  def buildRow(rowIndex: Long, rowWithIndexes: Iterable[(Long, Double)]): (Long, DenseVector[Double]) = {
    val it = rowWithIndexes.iterator
    val resArr =  DenseVector.zeros[Double](rowWithIndexes.size)

    while (it.hasNext) {
      val n = it.next()
      resArr(n._1.toInt) = n._2
    }

    (rowIndex, resArr)
  }

}
