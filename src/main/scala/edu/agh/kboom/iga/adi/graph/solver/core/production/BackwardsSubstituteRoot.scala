package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD, Vertex}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

sealed case class BackwardsSubstituteRootMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteRoot
}

case object BackwardsSubstituteRoot extends Production
  with BaseProduction[BackwardsSubstituteRootMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteRootMessage] = {
    Vertex.childPositionOf(dst.v)(ctx.tree) match {
      case LEFT_CHILD => Some(BackwardsSubstituteRootMessage(
        MatrixFactory.ofDim(src.mX) {
          _ (2 to -1, ::) += src.mX(0 until 4, ::)
        }
      ))
      case RIGHT_CHILD => Some(BackwardsSubstituteRootMessage(
        MatrixFactory.ofDim(src.mX) {
          _ (2 to -1, ::) += src.mX(2 until 6, ::)
        }
      ))
    }
  }

  override def consume(dst: IgaElement, msg: BackwardsSubstituteRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX += msg.cx
  }

}
