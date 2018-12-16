package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.move
import edu.agh.kboom.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD, Vertex}
import edu.agh.kboom.core.{IgaTaskContext, MatrixX}

sealed case class BackwardsSubstituteRootMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteRoot
}

case object BackwardsSubstituteRoot extends Production
  with BaseProduction[BackwardsSubstituteRootMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteRootMessage] = {
    partialBackwardsSubstitution(6, 6, ctx.mc.mesh.yDofs)(dst)

    Vertex.childPositionOf(dst.v)(ctx.tree) match {
      case LEFT_CHILD => Some(BackwardsSubstituteRootMessage(
        src.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)()(move(2, 0))
      ))
      case RIGHT_CHILD => Some(BackwardsSubstituteRootMessage(
        src.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(2, 0))(move(2, 0))
      ))
    }
  }

  override def consume(dst: IgaElement, msg: BackwardsSubstituteRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX.add(msg.cx)
  }

}
