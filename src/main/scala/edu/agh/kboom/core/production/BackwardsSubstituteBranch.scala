package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.{moveFromSource, move}
import edu.agh.kboom.core.{MatrixX, IgaTaskContext}
import edu.agh.kboom.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD, Vertex}

sealed case class BackwardsSubstituteBranchMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteBranch
}

/**
  * BS_2_6 + BS_1_5
  */
case object BackwardsSubstituteBranch extends Production
  with BaseProduction[BackwardsSubstituteBranchMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteBranchMessage] = {
    partialBackwardsSubstitution(2, 6, ctx.mc.mesh.yDofs)(src)
    swapDofs(0, 2, 6, ctx.mc.mesh.yDofs)(src)
    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(src)

    Vertex.childPositionOf(dst.v)(ctx.tree) match {
      case LEFT_CHILD => Some(BackwardsSubstituteBranchMessage(
        src.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)()(move(1, 0))
      ))
      case RIGHT_CHILD => Some(BackwardsSubstituteBranchMessage(
        src.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(2, 0))(move(1, 0))
      ))
    }
  }

  override def consume(dst: IgaElement, msg: BackwardsSubstituteBranchMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX.add(msg.cx)
    partialBackwardsSubstitution(1, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(0, 1, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(1, 2, 5, ctx.mc.mesh.yDofs)(dst)
  }

}
