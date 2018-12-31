package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.StepSolver.transposeRowMatrix
import edu.agh.kboom.iga.adi.graph.solver.core.{SplineSurface, Surface}
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.{HorizontalInitializer, VerticalInitializer}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}

case class StepSolver(directionSolver: DirectionSolver) {

  val loggingConfig = SolverConfig.LoadedSolverConfig.logging

  def solve(ctx: IgaContext)(surface: Surface)(implicit sc: SparkContext): SplineSurface = {
    val partialSolution = directionSolver.solve(ctx, HorizontalInitializer(surface, ctx.problem))
    val transposedPartialSolution = SplineSurface(transposeRowMatrix(partialSolution.m), ctx.mesh)

    partialSolution.m.rows.unpersist(blocking = false)

    if (loggingConfig.elements) {
      SplineSurface.print(transposedPartialSolution)
    }

    val newProjection = directionSolver.solve(ctx.changedDirection(), VerticalInitializer(transposedPartialSolution))

    if (loggingConfig.elements) {
      SplineSurface.print(newProjection)
    }
    newProjection
  }

}

object StepSolver {

  def transposeRowMatrix(m: IndexedRowMatrix): IndexedRowMatrix = {
    val transposedRowsRDD = m.rows.map(rowToTransposedTriplet)
      .flatMap(x => x) // now we have triplets (newRowIndex, (newColIndex, value))
      .groupByKey
      .map { case (a, b) => buildRow(a, b) }
    new IndexedRowMatrix(transposedRowsRDD)
  }

  def rowToTransposedTriplet(row: IndexedRow): Array[(Long, (Long, Double))] =
    row.vector.toArray.zipWithIndex.map { case (value, colIndex) => (colIndex.toLong, (row.index, value)) }

  def buildRow(rowIndex: Long, rowWithIndexes: Iterable[(Long, Double)]): IndexedRow = {
    val resArr = new Array[Double](rowWithIndexes.size)
    rowWithIndexes.foreach { case (index, value) =>
      resArr(index.toInt) = value
    }
    IndexedRow(rowIndex, Vectors.dense(resArr))
  }

}
