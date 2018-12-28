package edu.agh.kboom

import edu.agh.kboom.core._
import edu.agh.kboom.core.initialisation.{HorizontalInitializer, VerticalInitializer}
import edu.agh.kboom.core.tree.ProblemTree
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}
import org.apache.spark.sql.SparkSession


object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    implicit val sc = spark.sparkContext

    // OK: 12, 48
    val mesh = Mesh(12, 12, 12, 12)
    val solver = DirectionSolver(mesh)

    implicit val problemTree = ProblemTree(mesh.xSize)
    implicit val igaContext = IgaContext(mesh, LinearProblem.valueAt)

    val partialSolution = solver.solve(igaContext, HorizontalInitializer)
    val transposedPartialSolution = Solution(transposeRowMatrix(partialSolution.m))
    Solution.print(transposedPartialSolution)

    val solution = solver.solve(igaContext.changedDirection(), VerticalInitializer(transposedPartialSolution))

    Solution.print(solution)
    // transpose the matrix

    // https://stackoverflow.com/questions/30556478/matrix-transpose-on-rowmatrix-in-spark

    spark.stop()
  }

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
