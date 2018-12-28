package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.iga.adi.graph.solver.core.StepSolver.transposeRowMatrix
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.{HorizontalInitializer, VerticalInitializer}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}

case class StepSolver(directionSolver: DirectionSolver) {

  def solve(ctx: IgaContext)(implicit sc: SparkContext): Unit = {
    val partialSolution = directionSolver.solve(ctx, HorizontalInitializer)
    val transposedPartialSolution = Solution(transposeRowMatrix(partialSolution.m))
    Solution.print(transposedPartialSolution)

    val solution = directionSolver.solve(ctx.changedDirection(), VerticalInitializer(transposedPartialSolution))

    Solution.print(solution)
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
