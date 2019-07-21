package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD, Vertex}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

import scala.annotation.switch

sealed case class BackwardsSubstituteRootMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteRoot
}

case object BackwardsSubstituteRoot extends Production
  with BaseProduction[BackwardsSubstituteRootMessage] {

  private val r2tom1 = 2 to -1
  private val r0u4 = 0 until 4
  private val r2u6 = 2 until 6

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): BackwardsSubstituteRootMessage = {
    (Vertex.childPositionOf(dst.v)(ctx.tree): @switch) match {
      case LEFT_CHILD => BackwardsSubstituteRootMessage(
        MatrixFactory.ofDim(src.mX) {
          _ (r2tom1, ::) += src.mX(r0u4, ::)
        }
      )
      case RIGHT_CHILD => BackwardsSubstituteRootMessage(
        MatrixFactory.ofDim(src.mX) {
          _ (r2tom1, ::) += src.mX(r2u6, ::)
        }
      )
    }
  }

  override def consume(dst: IgaElement, msg: BackwardsSubstituteRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX += msg.cx
  }

}
