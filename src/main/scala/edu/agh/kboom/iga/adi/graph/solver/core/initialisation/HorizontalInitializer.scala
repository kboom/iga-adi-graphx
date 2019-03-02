package edu.agh.kboom.iga.adi.graph.solver.core.initialisation

import edu.agh.kboom.iga.adi.graph.solver.IgaContext
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

  def collocate(r: Iterator[IndexedRow])(implicit ctx: IgaContext): Iterator[(Vertex, (Int, Array[Double]))] =
    r.toList.flatMap { row =>
      val idx = row.index.toInt

      VerticalInitializer.verticesDependentOnRow(idx)
        .map(vertex => {
          val localRow = VerticalInitializer.findLocalRowFor(vertex, idx)
          val vertexRowValues = row.vector.toArray
          (vertex, (localRow, vertexRowValues))
        })
    }.iterator

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
      .mapPartitions(collocate(_)(ctx), preservesPartitioning = true)
      .groupBy(_._1.id.toLong)
      .mapValues(_.map(_._2))

    val leafIndices = firstIndexOfLeafRow to lastIndexOfLeafRow
    val elementsByVertex = sc.parallelize(leafIndices)
      .map(id => (id.toLong, id))
      .join(data)
      .mapPartitions(
        _.map { case (idx, d) =>
          val vertex = Vertex.vertexOf(idx.toInt)
          val value = d._2
          (idx.toLong, createElement(vertex, FromCoefficientsValueProvider(problem, value.toMap))(ctx))
        },
        preservesPartitioning = true
      )

    data.unpersist(blocking = false)
    elementsByVertex
  }

  private def createElement(v: Vertex, vp: ValueProvider)(implicit ctx: IgaContext): Element = {
    val e = Element.createForX(ctx.mesh)
    MethodCoefficients.bind(e.mA)
    for (i <- 0 until ctx.mesh.xDofs) {
      e.mB(0, i) = force(v, e, vp, 0, i)
      e.mB(1, i) = force(v, e, vp, 1, i)
      e.mB(2, i) = force(v, e, vp, 2, i)
    }
    e
  }

  private def force(v: Vertex, e: Element, vp: ValueProvider, r: Int, i: Int)(implicit ctx: IgaContext): Double = {
    implicit val problemTree: ProblemTree = ctx.tree()
    implicit val mesh: Mesh = ctx.mesh
    val segment = Vertex.segmentOf(v)._1

    val left = r match {
      case (0) => GaussPoint.S31
      case (1) => GaussPoint.S21
      case (2) => GaussPoint.S11
    }

    val center = r match {
      case (0) => GaussPoint.S32
      case (1) => GaussPoint.S22
      case (2) => GaussPoint.S12
    }

    val right = r match {
      case (0) => GaussPoint.S33
      case (1) => GaussPoint.S23
      case (2) => GaussPoint.S13
    }

    var value = 0.0

    for (k <- 0 until GaussPoint.gaussPointCount) {
      val gpk = GaussPoint.gaussPoints(k)
      val x = gpk.v * mesh.dx + segment

      for (l <- 0 until GaussPoint.gaussPointCount) {
        val gpl = GaussPoint.gaussPoints(l)
        if (i > 1) {
          val y = (gpl.v + i - 2) * mesh.dy
          value += left(k, l) * vp.valueAt(x, y)
        }
        if (i > 0 && (i - 1) < mesh.ySize) {
          val y = (gpl.v + i - 1) * mesh.dy
          value += center(k, l) * vp.valueAt(x, y)
        }
        if (i < mesh.ySize) {
          val y = (gpl.v + i) * mesh.dy
          value += right(k, l) * vp.valueAt(x, y)
        }
      }
    }

    value
  }

}
