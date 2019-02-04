package edu.agh.kboom.iga.adi.graph.solver.core.initialisation

import edu.agh.kboom.iga.adi.graph.solver.IgaContext
import edu.agh.kboom.iga.adi.graph.solver.core.Spline.{Spline1T, Spline2T, Spline3T}
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.HorizontalInitializer.collocate
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.{firstIndexOfLeafRow, lastIndexOfLeafRow}
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import org.apache.spark.SparkContext
import org.apache.spark.graphx.VertexId
import org.apache.spark.mllib.linalg.distributed.IndexedRow
import org.apache.spark.rdd.RDD

sealed trait ValueProvider {
  def valueAt(i: Double, j: Double): Double
}

case class FromCoefficientsValueProvider(problem: Problem, rows: Map[Int, Array[Double]]) extends ValueProvider {
  private val Extractor: (Int, Int) => Double = (i, j) => rows(i)(j)

  override def valueAt(x: Double, y: Double): Double = problem.valueAt(Extractor, x, y)
}

case class FromProblemValueProvider(problem: Problem) extends ValueProvider {
  private val Extractor: (Int, Int) => Double = (_, _) => 1

  override def valueAt(x: Double, y: Double): Double = problem.valueAt(Extractor, x, y)
}

object HorizontalInitializer {

  def collocate(row: IndexedRow)(implicit ctx: IgaContext): Seq[(Vertex, (Int, Array[Double]))] = {
    val idx = row.index.toInt

    VerticalInitializer.verticesDependentOnRow(idx)
      .map(vertex => {
        val localRow = VerticalInitializer.findLocalRowFor(vertex, idx)
        val vertexRowValues = row.vector.toArray
        (vertex, (localRow, vertexRowValues))
      })
  }

}

case class HorizontalInitializer(surface: Surface, problem: Problem) extends LeafInitializer {

  override def leafData(ctx: IgaContext)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    surface match {
      case PlainSurface(_) => initializeSurface(ctx)
      case s: SplineSurface => projectSurface(ctx, s)
    }
  }

  private def initializeSurface(ctx: IgaContext)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    implicit val tree: ProblemTree = ctx.xTree()
    val leafIndices = firstIndexOfLeafRow to lastIndexOfLeafRow
    sc.parallelize(leafIndices)
      .mapPartitions(_.toList.map { idx =>
        val vertex = Vertex.vertexOf(idx)
        (idx.toLong, createElement(vertex, FromProblemValueProvider(problem))(ctx))
      }.iterator, preservesPartitioning = true)
  }

  private def projectSurface(ctx: IgaContext, ss: SplineSurface)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    implicit val tree: ProblemTree = ctx.xTree()

    val data = ss.m.rows
      .flatMap(m => collocate(m)(ctx))
      .groupBy(_._1.id.toLong)
      .mapValues(_.map(_._2))

    val leafIndices = firstIndexOfLeafRow to lastIndexOfLeafRow
    val elementsByVertex = sc.parallelize(leafIndices)
      .map(id => (id.toLong, id))
      .join(data)
      .mapPartitions(
        _.toList.map { case (idx, d) =>
          val vertex = Vertex.vertexOf(idx.toInt)
          val value = d._2
          (idx.toLong, createElement(vertex, FromCoefficientsValueProvider(problem, value.toMap))(ctx))
        }.iterator,
        preservesPartitioning = true
      )

    data.unpersist(blocking = false)
    elementsByVertex
  }

  private def createElement(v: Vertex, vp: ValueProvider)(implicit ctx: IgaContext): Element = {
    val e = Element.createForX(ctx.mesh)
    MethodCoefficients.bind(e.mA)
    for (i <- 0 until ctx.mesh.xDofs) {
      fillRightHandSide(v, e, vp, Spline3T, 0, i)
      fillRightHandSide(v, e, vp, Spline2T, 1, i)
      fillRightHandSide(v, e, vp, Spline1T, 2, i)
    }
    e
  }

  private def fillRightHandSide(v: Vertex, e: Element, vp: ValueProvider, spline: Spline, r: Int, i: Int)(implicit ctx: IgaContext): Unit = {
    implicit val problemTree: ProblemTree = ctx.tree()
    implicit val mesh: Mesh = ctx.mesh

    for (k <- 0 until GaussPoint.gaussPointCount) {
      val gpk = GaussPoint.gaussPoints(k)
      val x = gpk.v * mesh.dx + Vertex.segmentOf(v)._1
      for (l <- 0 until GaussPoint.gaussPointCount) {
        val gpl = GaussPoint.gaussPoints(l)
        if (i > 1) {
          val y = (gpl.v + i - 2) * mesh.dy
          e.mB.mapEntry(r, i)(_ + gpk.w * spline.getValue(gpk.v) * gpl.w * Spline1T.getValue(gpl.v) * vp.valueAt(x, y))
        }
        if (i > 0 && (i - 1) < mesh.ySize) {
          val y = (gpl.v + i - 1) * mesh.dy
          e.mB.mapEntry(r, i)(_ + gpk.w * spline.getValue(gpk.v) * gpl.w * Spline2T.getValue(gpl.v) * vp.valueAt(x, y))
        }
        if (i < mesh.ySize) {
          val y = (gpl.v + i) * mesh.dy
          e.mB.mapEntry(r, i)(_ + gpk.w * spline.getValue(gpk.v) * gpl.w * Spline3T.getValue(gpl.v) * vp.valueAt(x, y))
        }
      }
    }
  }

}
