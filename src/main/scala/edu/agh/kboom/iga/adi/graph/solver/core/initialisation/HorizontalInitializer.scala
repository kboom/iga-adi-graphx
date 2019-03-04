package edu.agh.kboom.iga.adi.graph.solver.core.initialisation

import breeze.linalg.DenseMatrix
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

case class FromCoefficientsValueProvider(problem: Problem, m: DenseMatrix[Double]) extends ValueProvider {
  private val extractor = MatrixExtractor(m)

  override def valueAt(x: Double, y: Double): Double = problem.valueAt(extractor, x, y)
}

case class FromProblemValueProvider(problem: Problem) extends ValueProvider {
  override def valueAt(x: Double, y: Double): Double = problem.valueAt(NoExtractor, x, y)
}

object HorizontalInitializer {

  def collocate(r: Iterator[IndexedRow])(implicit ctx: IgaContext): Iterator[(Vertex, (Int, Array[Double]))] =
    r.flatMap { row =>
      val idx = row.index.toInt

      VerticalInitializer.verticesDependentOnRow(idx)
        .map(vertex => {
          val localRow = VerticalInitializer.findLocalRowFor(vertex, idx)
          (vertex, (localRow, row.vector.toArray))
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
      .mapPartitions(_.map { idx =>
        val vertex = Vertex.vertexOf(idx)
        (idx.toLong, createElement(vertex, FromProblemValueProvider(problem))(ctx))
      }, preservesPartitioning = true)
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
          val value = d._2.toMap
          (idx.toLong, createElement(vertex, FromCoefficientsValueProvider(problem, DenseMatrix(
            value(0),
            value(1),
            value(2)
          )))(ctx)) // todo this might be incorrect for more complex computations (column major)
        },
        preservesPartitioning = true
      )

    data.unpersist(blocking = false)
    elementsByVertex
  }

  private def createElement(v: Vertex, vp: ValueProvider)(implicit ctx: IgaContext): Element = {
    implicit val mesh: Mesh = ctx.mesh
    implicit val problemTree: ProblemTree = ctx.tree()
    val segment = Vertex.segmentOf(v)._1
    Element(
      mesh.xDofs,
      mA = MethodCoefficients.bound(Element.createMatA()),
      mB = DenseMatrix.tabulate(6, mesh.xDofs)((j, i) => if(j < 3) force(vp, segment, j, i) else 0),
      mX = Element.createMatX
    )
  }

  private def force(vp: ValueProvider, segment: Double, r: Int, i: Int)(implicit ctx: IgaContext): Double = {
    implicit val problemTree: ProblemTree = ctx.tree()
    implicit val mesh: Mesh = ctx.mesh


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
