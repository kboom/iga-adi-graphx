package edu.agh.kboom.production

import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.core.Array2D.{moveToDest, moveFromSource}
import edu.agh.kboom.core.ArrayX
import edu.agh.kboom.tree.{BoundElement, LEFT_CHILD, RIGHT_CHILD, Vertex}

sealed case class BackwardsSubstituteInterimMessage(cx: ArrayX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteInterim()
}

case object BackwardsSubstituteInterim extends Production
  with BaseProduction[BackwardsSubstituteInterimMessage] {

  override def emit(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteInterimMessage] = {
    partialBackwardsSubstitution(2, 6, ctx.mc.mesh.yDofs)(src)
    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(src)
    swapDofs(2, 4, 6, ctx.mc.mesh.yDofs)(src)

    Vertex.childPositionOf(dst.v)(ctx.tree) match {
      case LEFT_CHILD => Some(BackwardsSubstituteInterimMessage(
        src.mX.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)()(moveToDest(2, 0))
      ))
      case RIGHT_CHILD => Some(BackwardsSubstituteInterimMessage(
        src.mX.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)(moveToDest(2, 0))(moveFromSource(2, 0))
      ))
    }
  }

  override def consume(dst: BoundElement, msg: BackwardsSubstituteInterimMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX += msg.cx
  }

}
