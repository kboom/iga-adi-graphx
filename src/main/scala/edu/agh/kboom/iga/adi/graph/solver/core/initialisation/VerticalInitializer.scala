package edu.agh.kboom.iga.adi.graph.solver.core.initialisation

import breeze.linalg.{DenseMatrix, DenseVector}
import edu.agh.kboom.iga.adi.graph.solver.IgaContext
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.VerticalInitializer.collocate
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import org.apache.spark.{HashPartitioner, SparkContext}
import org.apache.spark.graphx.VertexId
import org.apache.spark.rdd.RDD

object VerticalInitializer {

  private val FIRST_PARTITION = Array(1d, 1 / 2d, 1 / 3d)
  private val SECOND_PARTITION = Array(1 / 2d, 1 / 3d, 1 / 3d)
  private val THIRD_PARTITION = Array(1 / 3d, 1 / 3d, 1 / 3d)

  private val MIDDLE_PARTITION = Array(1 / 3d, 1 / 3d, 1 / 3d)

  private val THIRD_TO_LAST_PARTITION = Array(1 / 3d, 1 / 3d, 1 / 3d)
  private val SECOND_TO_LAST_PARTITION = Array(1 / 3d, 1 / 3d, 1 / 2d)
  private val LAST_PARTITION = Array(1 / 3d, 1 / 2d, 1d)

  def verticesDependentOnRow(rowNo: Int)(implicit ctx: IgaContext): Seq[Vertex] = {
    implicit val tree: ProblemTree = ctx.yTree()
    val elements = ctx.mesh.yDofs

    val all = Seq(-1, 0, 1)
      .view
      .map(_ + ProblemTree.firstIndexOfLeafRow)
      .map(_ + rowNo - 1)
      .filterNot { x => x < ProblemTree.firstIndexOfLeafRow || x > ProblemTree.lastIndexOfLeafRow }
      .map(Vertex.vertexOf)


    val span = Math.min(3, 1 + Math.min(rowNo, elements - 1 - rowNo))

    if (rowNo < elements / 2) all.take(span) else all.takeRight(span)
  }

  def findLocalRowFor(v: Vertex, rowNo: Int)(implicit ctx: IgaContext): Int = {
    implicit val tree: ProblemTree = ctx.yTree()
    (rowNo - Vertex.offsetLeft(v)).toInt
  }

  def findPartitionFor(v: Vertex, rowNo: Int)(implicit ctx: IgaContext): Double = {
    implicit val tree: ProblemTree = ctx.yTree()
    val localRow = findLocalRowFor(v, rowNo)
    val firstIdx = ProblemTree.firstIndexOfLeafRow
    val lastIdx = ProblemTree.lastIndexOfLeafRow
    v.id match {
      case x if x == firstIdx => FIRST_PARTITION(localRow)
      case x if x == firstIdx + 1 => SECOND_PARTITION(localRow)
      case x if x == firstIdx + 2 => THIRD_PARTITION(localRow)
      case x if x == lastIdx - 2 => THIRD_TO_LAST_PARTITION(localRow)
      case x if x == lastIdx - 1 => SECOND_TO_LAST_PARTITION(localRow)
      case x if x == lastIdx => LAST_PARTITION(localRow)
      case _ => MIDDLE_PARTITION(localRow)
    }
  }

  def collocate(r: Iterator[(Long, DenseVector[Double])])(implicit ctx: IgaContext): Iterator[(Long, Seq[(Int, DenseVector[Double])])] =
    r.flatMap { row =>
      val idx = row._1.toInt

      VerticalInitializer.verticesDependentOnRow(idx)
        .map(vertex => {
          val localRow = findLocalRowFor(vertex, idx)
          val partition = findPartitionFor(vertex, idx)
          val vertexRowValues = row._2.map(_ * partition)
          (vertex.id, Seq((localRow, vertexRowValues)))
        })
    }
}

case class VerticalInitializer(hsi: SplineSurface) extends LeafInitializer {

  override def leafData(ctx: IgaContext)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    implicit val tree: ProblemTree = ctx.yTree()

    hsi.m
      .mapPartitions(collocate(_)(ctx))
      .reduceByKey(_ ++ _)
      .mapPartitions(
        _.map { case (idx, d) =>
          val vertex = Vertex.vertexOf(idx)
          val dx = d.toMap
          (idx.toLong, createElement(vertex, DenseMatrix(
            dx(0),
            dx(1),
            dx(2) // todo this might sl be a problem due to column major approach
          ))(ctx))
        }
      )
  }

  def createElement(v: Vertex, rows: DenseMatrix[Double])(implicit ctx: IgaContext): Element = {
    val e = Element.createForX(ctx.mesh)
    MethodCoefficients.bound(e.mA)
    e.mB(0 until 3, ::) += rows
    e
  }

}

