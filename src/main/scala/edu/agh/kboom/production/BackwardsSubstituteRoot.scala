package edu.agh.kboom.production

import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.core.Array2D.{moveToDest, moveFromSource}
import edu.agh.kboom.core.ArrayX
import edu.agh.kboom.tree.{BoundElement, LEFT_CHILD, RIGHT_CHILD, Vertex}

sealed case class BackwardsSubstituteRootMessage(cx: ArrayX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteBranch()
}

case object BackwardsSubstituteRoot extends Production
  with BaseProduction[BackwardsSubstituteRootMessage] {

  override def send(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteRootMessage] = Vertex.childPositionOf(dst.v)(ctx.tree) match {
    case LEFT_CHILD => Some(BackwardsSubstituteRootMessage(
      src.mX.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)()(moveToDest(2, 0))
    ))
    case RIGHT_CHILD => Some(BackwardsSubstituteRootMessage(
      src.mX.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)(moveToDest(2, 0))(moveFromSource(2, 0))
    ))
  }

  override def receive(dst: BoundElement, msg: BackwardsSubstituteRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX += msg.cx
    partialBackwardsSubstitution(2, 6, ctx.mc.mesh.yDofs)(dst)
    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(dst)
    swapDofs(2, 4, 6, ctx.mc.mesh.yDofs)(dst)
  }

}
