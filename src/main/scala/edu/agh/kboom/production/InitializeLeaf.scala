package edu.agh.kboom.production

import edu.agh.kboom._
import edu.agh.kboom.tree.{Vertex, _}

case class InitializeLeaf() extends Production

object InitializeLeaf {

  def run(p: InitializeLeaf, e: BoundElement)(implicit ctx: IgaTaskContext): Unit = {
    MethodCoefficients.bind(e.mA)
    for (i <- 1 to ctx.mc.mesh.xDofs) {
      fillRightHandSide(e, Spline1(), 1, i)
      fillRightHandSide(e, Spline2(), 2, i)
      fillRightHandSide(e, Spline3(), 3, i)
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
          e.mB(r)(i) += gpk.v * spline.getValue(gpk.v) * gpl.w * Spline1().getValue(gpl.v) * ctx.mc.problem(x, y)
        }
        if (i > 1 && (i - 2) < mesh.ySize) {
          val y = (gpl.v + i - 2) * mesh.dy
          e.mB(r)(i) += gpk.v * spline.getValue(gpk.v) * gpl.w * Spline2().getValue(gpl.v) * ctx.mc.problem(x, y)
        }
        if ((i - 1) < mesh.ySize) {
          val y = (gpl.v + i - 1) * mesh.dy
          e.mB(r)(i) += gpk.v * spline.getValue(gpk.v) * gpl.w * Spline3().getValue(gpl.v) * ctx.mc.problem(x, y)
        }
      }
    }
  }

}