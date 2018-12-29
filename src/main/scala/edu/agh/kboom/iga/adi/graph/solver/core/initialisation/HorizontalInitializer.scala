package edu.agh.kboom.iga.adi.graph.solver.core.initialisation

import edu.agh.kboom.iga.adi.graph.solver.IgaContext
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.{firstIndexOfLeafRow, lastIndexOfLeafRow}
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import org.apache.spark.SparkContext
import org.apache.spark.graphx.VertexId
import org.apache.spark.rdd.RDD

object HorizontalInitializer {

}

case class HorizontalInitializer(hsi: Projection, problem: Problem) extends LeafInitializer {

  override def leafData(ctx: IgaContext)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    implicit val tree = ctx.xTree()

    val data = hsi.m.rows
      .flatMap(m => VerticalInitializer.collocate(m)(ctx))
      .groupBy(_._1.id.toLong)
      .mapValues(_.map(_._2))

    println(data.collect().map {
      case (vid, value) => f"$vid: ${value}"
    }.mkString(", "))

    val leafIndices = firstIndexOfLeafRow to lastIndexOfLeafRow
    sc.parallelize(leafIndices)
      .map(id => (id.toLong, id))
      .join(data)
      .map { case (idx, d) => {
        val vertex = Vertex.vertexOf(idx.toInt)
        val value = d._2
        (idx.toLong, createElement(vertex, value.toMap, problem)(ctx))
      }
      }

  }

  private def createElement(v: Vertex, rows: Map[Int, Array[Double]], problem: Problem)(implicit ctx: IgaContext): Element = {
    val e = Element.createForX(ctx.mesh)
    MethodCoefficients.bind(e.mA)
    for (i <- 0 until ctx.mesh.xDofs) {
      fillRightHandSide(v, e, rows, problem, Spline3(), 0, i)
      fillRightHandSide(v, e, rows, problem, Spline2(), 1, i)
      fillRightHandSide(v, e, rows, problem, Spline1(), 2, i)
    }
    e
  }

  private def fillRightHandSide(v: Vertex, e: Element, rows: Map[Int, Array[Double]], problem: Problem, spline: Spline, r: Int, i: Int)(implicit ctx: IgaContext): Unit = {
    implicit val problemTree: ProblemTree = ctx.tree()
    implicit val mesh: Mesh = ctx.mesh

    val fromRows: (Int, Int) => Double = (i, j) => rows(i)(j)

    for (k <- 0 until GaussPoint.gaussPointCount) {
      val gpk = GaussPoint.gaussPoints(k)
      val x = gpk.v * mesh.dx + Vertex.segmentOf(v)._1
      for (l <- 0 until GaussPoint.gaussPointCount) {
        val gpl = GaussPoint.gaussPoints(l)
        if (i > 1) {
          val y = (gpl.v + i - 2) * mesh.dy
          e.mB.mapEntry(r, i)(_ + gpk.w * spline.getValue(gpk.v) * gpl.w * Spline1().getValue(gpl.v) * problem.valueAt(fromRows, x, y))
        }
        if (i > 0 && (i - 1) < mesh.ySize) {
          val y = (gpl.v + i - 1) * mesh.dy
          e.mB.mapEntry(r, i)(_ + gpk.w * spline.getValue(gpk.v) * gpl.w * Spline2().getValue(gpl.v) * problem.valueAt(fromRows, x, y))
        }
        if (i < mesh.ySize) {
          val y = (gpl.v + i) * mesh.dy
          e.mB.mapEntry(r, i)(_ + gpk.w * spline.getValue(gpk.v) * gpl.w * Spline3().getValue(gpl.v) * problem.valueAt(fromRows, x, y))
        }
      }
    }
  }

}
