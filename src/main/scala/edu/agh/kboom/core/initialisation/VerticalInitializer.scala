package edu.agh.kboom.core.initialisation

import edu.agh.kboom.core._
import edu.agh.kboom.core.tree.ProblemTree.{firstIndexOfLeafRow, lastIndexOfLeafRow}
import edu.agh.kboom.core.tree._
import org.apache.spark.SparkContext
import org.apache.spark.graphx.VertexId
import org.apache.spark.mllib.linalg.distributed.IndexedRow
import org.apache.spark.rdd.RDD

case class VerticalInitializer(hsi: Solution) extends LeafInitializer {

  override def leafData(ctx: IgaContext)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    implicit val tree = ctx.yTree()

    val data = hsi.m.rows.groupBy(ir => Math.floor(ir.index.toInt / 3).toInt)

    println(data.keys.collect().mkString(", "))

    val leafIndices = firstIndexOfLeafRow to lastIndexOfLeafRow
    sc.parallelize(leafIndices)
      .map(id => (id, id))
      .join(data)
      .map { case (idx, d) => (idx.toLong, createElement(Vertex.vertexOf(idx), d._2)(ctx)) }
  }

  def createElement(v: Vertex, rows: Iterable[IndexedRow])(implicit ctx: IgaContext): Element = {
    val e = Element.createForX(ctx.mesh)
    MethodCoefficients.bind(e.mA)
    initializeRightHandSides(e, rows.toSeq)
    e
  }

  private def initializeRightHandSides(e: Element, rows: Seq[IndexedRow])(implicit ctx: IgaContext): Unit = {
    implicit val problemTree: ProblemTree = ctx.yTree()
    implicit val mesh: Mesh = ctx.mesh

    val partition = findPartition(e)

    val leftVector = rows(0)
    val middleVector = rows(1)
    val rightVector = rows(2)

    for (i <- 0 until mesh.yDofs) {
      e.mB.replace(0, i, partition.left * leftVector.vector(i))
      e.mB.replace(1, i, partition.middle * middleVector.vector(i))
      e.mB.replace(2, i, partition.right * rightVector.vector(i))
    }
  }

  def findPartition(e: Element) = Partition(0, 0, 0, 0)

}
