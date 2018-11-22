package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.{moveFromSource, moveToDest}
import edu.agh.kboom.core.{ArrayX, IgaTaskContext}
import edu.agh.kboom.core.tree.{BoundElement, LEFT_CHILD, RIGHT_CHILD, Vertex}

sealed case class BackwardsSubstituteBranchMessage(cx: ArrayX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteBranch
}

case object BackwardsSubstituteBranch extends Production
  with BaseProduction[BackwardsSubstituteBranchMessage] {

  override def emit(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteBranchMessage] = {
    partialBackwardsSubstitution(2, 6, ctx.mc.mesh.yDofs)(dst)
    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(src)
    swapDofs(2, 4, 6, ctx.mc.mesh.yDofs)(src)

    Vertex.childPositionOf(dst.v)(ctx.tree) match {
      case LEFT_CHILD => Some(BackwardsSubstituteBranchMessage(
        src.mX.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)()(moveToDest(1, 0))
      ))
      case RIGHT_CHILD => Some(BackwardsSubstituteBranchMessage(
        src.mX.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)(moveToDest(1, 0))(moveFromSource(2, 0))
      ))
    }
  }

  override def consume(dst: BoundElement, msg: BackwardsSubstituteBranchMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX.add(msg.cx)
    partialBackwardsSubstitution(1, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(1, 2, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(2, 3, 5, ctx.mc.mesh.yDofs)(dst)
  }

}
