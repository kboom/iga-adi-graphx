package edu.agh.kboom.core.production

import edu.agh.kboom.core._
import edu.agh.kboom.core.tree.{Vertex, _}

sealed case class InitializeLeafMessage() extends ProductionMessage {
  override val production: Production = InitializeLeaf
}

object InitializeLeaf extends Production {

  def initialize(e: BoundElement)(implicit ctx: IgaTaskContext): Unit = {
    MethodCoefficients.bind(e.mA)
    for (i <- 0 until ctx.mc.mesh.xDofs) {
      fillRightHandSide(e, Spline1(), 0, i)
      fillRightHandSide(e, Spline2(), 1, i)
      fillRightHandSide(e, Spline3(), 2, i)
    }
  }

  private def fillRightHandSide(e: BoundElement, spline: Spline, r: Int, i: Int)(implicit ctx: IgaTaskContext): Unit = {
    implicit val problemTree: ProblemTree = ctx.mc.xTree()
    implicit val mesh: Mesh = ctx.mc.mesh

    for (k <- 0 until GaussPoint.gaussPointCount) {
      val gpk = GaussPoint.gaussPoints(k)
      val x = gpk.v * mesh.xRes + Vertex.segmentOf(e.v)._1
      for (l <- 0 until GaussPoint.gaussPointCount) {
        val gpl = GaussPoint.gaussPoints(l)
        if (i > 2) {
          val y = (gpl.v + i - 3) * mesh.dy
          e.mB.replace(r, i, gpk.v * spline.getValue(gpk.v) * gpl.w * Spline1().getValue(gpl.v) * ctx.mc.problem(x, y))
        }
        if (i > 1 && (i - 2) < mesh.ySize) {
          val y = (gpl.v + i - 2) * mesh.dy
          e.mB.replace(r, i, gpk.v * spline.getValue(gpk.v) * gpl.w * Spline2().getValue(gpl.v) * ctx.mc.problem(x, y))
        }
        if ((i - 1) < mesh.ySize) {
          val y = (gpl.v + i - 1) * mesh.dy
          e.mB.replace(r, i, gpk.v * spline.getValue(gpk.v) * gpl.w * Spline3().getValue(gpl.v) * ctx.mc.problem(x, y))
        }
      }
    }
  }

}