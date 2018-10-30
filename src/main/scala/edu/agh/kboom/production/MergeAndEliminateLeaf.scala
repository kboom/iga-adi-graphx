package edu.agh.kboom.production

import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.core.Array2D.{move, moveDown}
import edu.agh.kboom.core.{ArrayA, ArrayB}
import edu.agh.kboom.tree.Vertex.childPositionOf
import edu.agh.kboom.tree._

case class MergeAndEliminateLeafMessage(ca: ArrayA, cb: ArrayB) extends Production

object MergeAndEliminateLeaf extends Production
  with SendAndReceive[MergeAndEliminateLeafMessage]
  with Merge[MergeAndEliminateLeafMessage] {

  override def send(src: BoundElement)(implicit ctx: IgaTaskContext): Seq[MergeAndEliminateLeafMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Seq(MergeAndEliminateLeafMessage(
      src.mA,
      src.mB
    ))
    case MIDDLE_CHILD => Seq(MergeAndEliminateLeafMessage(
      src.mA.transformedBy(1 to 3, 1 to 3)(move(1, 1))(),
      src.mB.transformedBy(1 to 3, 1 to ctx.mc.mesh.yDofs)(moveDown(1))()
    ))
    case RIGHT_CHILD => Seq(MergeAndEliminateLeafMessage(
      src.mA.transformedBy(1 to 3, 1 to 3)(move(2, 2))(),
      src.mB.transformedBy(1 to 3, 1 to ctx.mc.mesh.yDofs)(moveDown(2))()
    ))
  }

  override def merge(a: MergeAndEliminateLeafMessage, b: MergeAndEliminateLeafMessage): MergeAndEliminateLeafMessage = MergeAndEliminateLeafMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def receive(dst: BoundElement, msg: MergeAndEliminateLeafMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    swapDofs(1, 3, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(2, 3, 5, ctx.mc.mesh.yDofs)(dst)
  }

}