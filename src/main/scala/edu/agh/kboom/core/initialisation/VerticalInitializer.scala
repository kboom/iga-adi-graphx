package edu.agh.kboom.core.initialisation

import edu.agh.kboom.core._
import edu.agh.kboom.core.tree.ProblemTree.{firstIndexOfLeafRow, lastIndexOfLeafRow}
import edu.agh.kboom.core.tree._
import org.apache.spark.SparkContext
import org.apache.spark.graphx.VertexId
import org.apache.spark.mllib.linalg.distributed.IndexedRow
import org.apache.spark.rdd.RDD

object VerticalInitializer {

  /**
    * 0,1,2 --  8
    * 1,2,3 --  9
    * 2,3,4 -- 10
    * 3,4,5 -- 11
    * (...)
    * 9,10,11 - 17
    * 10,11,12 - 18
    * 11,12,13 - 19
    */
  def verticesDependentOnRow(rowNo: Int)(implicit ctx: IgaContext): Seq[Int] = {
    implicit val tree = ctx.yTree()
    val elements = ctx.mesh.yDofs

    val all = Seq(-1, 0, 1)
      .map(_ + ProblemTree.firstIndexOfLeafRow)
      .map(_ + rowNo - 1)
      .filterNot { x => x < ProblemTree.firstIndexOfLeafRow || x > ProblemTree.lastIndexOfLeafRow }


    val span = Math.min(3, 1 + Math.min(rowNo, elements - 1 - rowNo))

    return if (rowNo < elements / 2) all.take(span) else all.takeRight(span)
  }

}

case class VerticalInitializer(hsi: Solution) extends LeafInitializer {

  override def leafData(ctx: IgaContext)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    implicit val tree = ctx.yTree()

    val findPartitionFor = (vid: Int, rowNo: Int) => 1

    val findLocalRowFor = (vid: Int, rowNo: Int) => 1

    /*
id = {0,1,2,3...N}

node.m_b(1)(i) = partition(0) * solution(i)(idx)
node.m_b(2)(i) = partition(1) * solution(i)(idx + 1)
node.m_b(3)(i) = partition(2) * solution(i)(idx + 2)

0,1,2
1,2,3
2,3,4
3,4,5
*/
    val collocate = (row: IndexedRow) => {
      val leafCount = ProblemTree.strengthOfLeaves()
      val idx = row.index.toInt

      VerticalInitializer.verticesDependentOnRow(idx)(ctx)
        .flatMap(vid => {
          val localRow = findLocalRowFor(vid, idx)
          val partition = findPartitionFor(vid, idx)
          val vertexRowValues = row.vector.toArray.map(_ * partition)
          Seq((vid.toLong, Seq((localRow, vertexRowValues))))
        })
    }

    val data = hsi.m.rows
      .flatMap(collocate)
      .groupBy(_._1)
      .flatMapValues(_.map(_._2))

    println(data.collect().map {
      case (vid, value) => f"$vid: ${value.map(_._1)}"
    }.mkString(", "))

    val leafIndices = firstIndexOfLeafRow to lastIndexOfLeafRow
    sc.parallelize(leafIndices)
      .map(id => (id.toLong, id))
      .join(data)
      .map { case (idx, d) => (idx.toLong, createElement(Vertex.vertexOf(idx.toInt), d._2.toMap)(ctx)) }
  }

  def createElement(v: Vertex, rows: Map[Int, Array[Double]])(implicit ctx: IgaContext): Element = {
    val e = Element.createForX(ctx.mesh)
    MethodCoefficients.bind(e.mA)
    for (r <- 0 until 3) {
      e.mB.replaceRow(r, rows(r))
    }
    e
  }

}
