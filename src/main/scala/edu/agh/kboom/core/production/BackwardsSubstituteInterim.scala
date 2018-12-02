package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.{moveFromSource, move}
import edu.agh.kboom.core.{MatrixX, IgaTaskContext}
import edu.agh.kboom.core.tree.{BoundElement, LEFT_CHILD, RIGHT_CHILD, Vertex}

sealed case class BackwardsSubstituteInterimMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteInterim
}

case object BackwardsSubstituteInterim extends Production
  with BaseProduction[BackwardsSubstituteInterimMessage] {

  override def emit(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteInterimMessage] = {
    partialBackwardsSubstitution(2, 6, ctx.mc.mesh.yDofs)(src)
    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(src)
    swapDofs(2, 4, 6, ctx.mc.mesh.yDofs)(src)

    Vertex.childPositionOf(dst.v)(ctx.tree) match {
      case LEFT_CHILD => Some(BackwardsSubstituteInterimMessage(
        src.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)()(move(2, 0))
      ))
      case RIGHT_CHILD => Some(BackwardsSubstituteInterimMessage(
        src.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(2, 0))(moveFromSource(2, 0))
      ))
    }
  }

  override def consume(dst: BoundElement, msg: BackwardsSubstituteInterimMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX.add(msg.cx)
  }

}
