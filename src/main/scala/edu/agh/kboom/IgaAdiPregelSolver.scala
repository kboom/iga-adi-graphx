package edu.agh.kboom

import edu.agh.kboom.core.production.{InitializeLeafMessage, ProductionMessage}
import edu.agh.kboom.core.tree.ProblemTree.{firstIndexOfBranchingRow, lastIndexOfBranchingRow}
import edu.agh.kboom.core.tree._
import edu.agh.kboom.core.{IgaContext, IgaOperation, IgaTasks, Mesh}
import org.apache.spark.graphx._
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix, RowMatrix}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    val sc = spark.sparkContext

    val problemTree = ProblemTree(12)
    val igaMesh = Mesh(12, 12, 12, 12)

    val edges: RDD[Edge[IgaOperation]] =
      sc.parallelize(
        IgaTasks.generateOperations(problemTree)
          .map(e => Edge(e.src.id, e.dst.id, e))
      )

    val dataItemGraph = Graph.fromEdges(edges, None)
      .mapVertices((vid, _) => IgaElement(Vertex.vertexOf(vid.toInt)(problemTree), Element.createForX(igaMesh)))

    implicit val program: VertexProgram = VertexProgram(IgaContext(igaMesh, (x, y) => 1))

    val initialMessage = InitializeLeafMessage().asInstanceOf[ProductionMessage]

    val result =
      dataItemGraph.pregel(initialMessage, activeDirection = EdgeDirection.Out)(
        VertexProgram.run,
        VertexProgram.sendMsg,
        VertexProgram.mergeMsg
      )

    val hs = new IndexedRowMatrix(result.vertices
      .filterByRange(firstIndexOfBranchingRow(problemTree), lastIndexOfBranchingRow(problemTree))
      .map { case (v, e) => (v - firstIndexOfBranchingRow(problemTree), e) }
      .map { case (v, be) => if (v == 0) (v, be.e.mX.arr.dropRight(1)) else (v, be.e.mX.arr.drop(2).dropRight(1)) }
      .flatMap { case (vid, be) => be.map(Vectors.dense).zipWithIndex.map { case (v, i) => if (vid == 0) IndexedRow(i, v) else IndexedRow(5 + (vid - 1) * 3 + i, v) } }
    )

    println(f"Size ${hs.numRows()}x${hs.numCols()}")

    printMatrix(hs)

    val vs = transposeRowMatrix(hs)

    println(f"Transposed ${vs.numRows()}x${vs.numCols()}")
    printMatrix(vs)



    // transpose the matrix

    // https://stackoverflow.com/questions/30556478/matrix-transpose-on-rowmatrix-in-spark

    spark.stop()
  }

  private def printMatrix(hs: IndexedRowMatrix) = {
    hs
      .rows // Extract RDD[org.apache.spark.mllib.linalg.Vector]
      .sortBy(_.index)
      .map(_.vector.toArray.map(i => f"$i%+.3f").mkString(" "))
      .collect // you can use toLocalIterator to limit memory usage
      .foreach(println) // Iterate over local Iterator and print
  }

  def transposeRowMatrix(m: IndexedRowMatrix): IndexedRowMatrix = {
    val transposedRowsRDD = m.rows.map(rowToTransposedTriplet)
      .flatMap(x => x) // now we have triplets (newRowIndex, (newColIndex, value))
      .groupByKey
      .map { case (a,b) => buildRow(a, b) }
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
