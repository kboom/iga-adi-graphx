package edu.agh.kboom.core.initialisation

import edu.agh.kboom.core._
import edu.agh.kboom.core.tree.ProblemTree.{firstIndexOfLeafRow, lastIndexOfLeafRow}
import edu.agh.kboom.core.tree._
import org.apache.spark.SparkContext
import org.apache.spark.graphx.VertexId
import org.apache.spark.rdd.RDD

object HorizontalInitializer extends LeafInitializer {

  override def leafData(ctx: IgaContext)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    implicit val tree = ctx.xTree()
    val leafIndices = firstIndexOfLeafRow to lastIndexOfLeafRow
    sc.parallelize(leafIndices)
      .map(idx => (idx.toLong, createElement(Vertex.vertexOf(idx))(ctx)))
  }

  private def createElement(v: Vertex)(implicit ctx: IgaContext): Element = {
    val e = Element.createForX(ctx.mesh)
    MethodCoefficients.bind(e.mA)
    for (i <- 0 until ctx.mesh.xDofs) {
      fillRightHandSide(v, e, Spline3(), 0, i)
      fillRightHandSide(v, e, Spline2(), 1, i)
      fillRightHandSide(v, e, Spline1(), 2, i)
    }
    e
  }

  private def fillRightHandSide(v: Vertex, e: Element, spline: Spline, r: Int, i: Int)(implicit ctx: IgaContext): Unit = {
    implicit val problemTree: ProblemTree = ctx.xTree()
    implicit val mesh: Mesh = ctx.mesh

    for (k <- 0 until GaussPoint.gaussPointCount) {
      val gpk = GaussPoint.gaussPoints(k)
      val x = gpk.v * mesh.xRes + Vertex.segmentOf(v)._1
      for (l <- 0 until GaussPoint.gaussPointCount) {
        val gpl = GaussPoint.gaussPoints(l)
        if (i > 1) {
          val y = (gpl.v + i - 2) * mesh.dy
          e.mB.mapEntry(r, i)(_ + gpk.w * spline.getValue(gpk.v) * gpl.w * Spline1().getValue(gpl.v) * ctx.problem(x, y))
        }
        if (i > 0 && (i - 1) < mesh.ySize) {
          val y = (gpl.v + i - 1) * mesh.dy
          e.mB.mapEntry(r, i)(_ + gpk.w * spline.getValue(gpk.v) * gpl.w * Spline2().getValue(gpl.v) * ctx.problem(x, y))
        }
        if (i < mesh.ySize) {
          val y = (gpl.v + i) * mesh.dy
          e.mB.mapEntry(r, i)(_ + gpk.w * spline.getValue(gpk.v) * gpl.w * Spline3().getValue(gpl.v) * ctx.problem(x, y))
        }
      }
    }
  }

}