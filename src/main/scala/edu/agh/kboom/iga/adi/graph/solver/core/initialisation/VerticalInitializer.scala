package edu.agh.kboom.iga.adi.graph.solver.core.initialisation

import breeze.linalg.DenseMatrix
import edu.agh.kboom.iga.adi.graph.solver.IgaContext
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.VerticalInitializer.collocate
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.{firstIndexOfLeafRow, lastIndexOfLeafRow}
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import org.apache.spark.SparkContext
import org.apache.spark.graphx.VertexId
import org.apache.spark.mllib.linalg.distributed.IndexedRow
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
      .map(_ + ProblemTree.firstIndexOfLeafRow)
      .map(_ + rowNo - 1)
      .filterNot { x => x < ProblemTree.firstIndexOfLeafRow || x > ProblemTree.lastIndexOfLeafRow }
      .map(Vertex.vertexOf)


    val span = Math.min(3, 1 + Math.min(rowNo, elements - 1 - rowNo))

    if (rowNo < elements / 2) all.take(span) else all.takeRight(span)
  }

  def findLocalRowFor(v: Vertex, rowNo: Int)(implicit ctx: IgaContext): Int = {
    implicit val tree: ProblemTree = ctx.yTree()
    rowNo - Vertex.offsetLeft(v)
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

  def collocate(r: Iterator[IndexedRow])(implicit ctx: IgaContext): Iterator[(Vertex, (Int, Array[Double]))] =
    r.toList.flatMap { row =>
      val idx = row.index.toInt

      VerticalInitializer.verticesDependentOnRow(idx)
        .map(vertex => {
          val localRow = findLocalRowFor(vertex, idx)
          val partition = findPartitionFor(vertex, idx)
          val vertexRowValues = row.vector.toArray.map(_ * partition)
          (vertex, (localRow, vertexRowValues))
        })
    }.iterator
}

case class VerticalInitializer(hsi: SplineSurface) extends LeafInitializer {

  override def leafData(ctx: IgaContext)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    implicit val tree: ProblemTree = ctx.yTree()

    val data = hsi.m.rows
      .mapPartitions(collocate(_)(ctx))
      .groupBy(_._1.id.toLong)
      .mapValues(_.map(_._2))

    val leafIndices = firstIndexOfLeafRow to lastIndexOfLeafRow
    val elementsByVertexId = sc.parallelize(leafIndices)
      .mapPartitions(_.toList.map { id => (id.toLong, id) }.iterator, preservesPartitioning = true)
      .join(data)
      .mapPartitions(
        _.toList.map { case (idx, d) =>
          val vertex = Vertex.vertexOf(idx.toInt)
          val value = d._2.toMap
          (idx.toLong, createElement(vertex, DenseMatrix.create(3, ctx.mesh.yDofs,
            value(0) ++ value(1) ++ value(2) // todo is this costly?
          ))(ctx))
        }.iterator,
        preservesPartitioning = true
      )

    elementsByVertexId
  }

  def createElement(v: Vertex, rows: DenseMatrix[Double])(implicit ctx: IgaContext): Element = {
    val e = Element.createForX(ctx.mesh)
    MethodCoefficients.bind(e.mA)
    e.mB(0 until 3, ::) := rows
    e
  }

}
