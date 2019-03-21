package edu.agh.kboom.iga.adi.graph.solver.core.initialisation

import breeze.linalg.{DenseMatrix, DenseVector}
import edu.agh.kboom.iga.adi.graph.solver.{IgaContext, VertexPartitioner}
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.HorizontalInitializer.collocate
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.{firstIndexOfLeafRow, lastIndexOfLeafRow}
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import org.apache.spark.{HashPartitioner, SparkContext}
import org.apache.spark.graphx.VertexId
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

  def collocate(r: Iterator[(Long, DenseVector[Double])])(implicit ctx: IgaContext): Iterator[(Long, Seq[(Int, DenseVector[Double])])] =
    r.flatMap { row =>
      val idx = row._1.toInt

      VerticalInitializer.verticesDependentOnRow(idx)
        .map(vertex => {
          val localRow = VerticalInitializer.findLocalRowFor(vertex, idx)
          (vertex.id, Seq((localRow, row._2)))
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
    sc.parallelize(leafIndices.map((_, None)))
      .partitionBy(VertexPartitioner(sc.defaultParallelism, tree))
      .mapPartitions(_.map { case (idx, _) =>
        val vertex = Vertex.vertexOf(idx)
        (idx, createElement(vertex, FromProblemValueProvider(problem))(ctx))
      }, preservesPartitioning = true)
      .cache()
  }

  private def projectSurface(ctx: IgaContext, ss: SplineSurface)(implicit sc: SparkContext): RDD[(VertexId, Element)] = {
    implicit val tree: ProblemTree = ctx.xTree()

    // better distribute the shit!!!! as of now only 2 nodes involved, locality=0 helps but will crash for greater problem sizes!
    val partitioner = ss.m.partitioner.getOrElse(new HashPartitioner(sc.defaultParallelism))

    ss.m
      .mapPartitions(collocate(_)(ctx))
      .reduceByKey(partitioner, _ ++ _)
      .mapPartitions(
        _.map { case (vid, s) =>
          val v = Vertex.vertexOf(vid)
          val dx = s.toMap
          (vid.toLong, createElement(v, FromCoefficientsValueProvider(problem, DenseMatrix(
            dx(0),
            dx(1),
            dx(2)
          )))(ctx)) // todo this might be incorrect for more complex computations (column major)
        },
        preservesPartitioning = true
      )
  }

  private def createElement(v: Vertex, vp: ValueProvider)(implicit ctx: IgaContext): Element = {
    implicit val mesh: Mesh = ctx.mesh
    implicit val problemTree: ProblemTree = ctx.tree()
    val segment = Vertex.segmentOf(v)._1
    Element(
      mesh.xDofs,
      mA = MethodCoefficients.bound(Element.createMatA()),
      mB = DenseMatrix.tabulate(6, mesh.xDofs)((j, i) => if (j < 3) force(vp, segment, j, i) else 0),
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
