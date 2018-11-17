package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.moveToDest
import edu.agh.kboom.core.tree.Vertex.childPositionOf
import edu.agh.kboom.core.tree._
import edu.agh.kboom.core.{ArrayA, ArrayB, IgaTaskContext}

case class MergeAndEliminateLeafMessage(ca: ArrayA, cb: ArrayB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateLeaf
}

case object MergeAndEliminateLeaf extends Production
  with BaseProduction[MergeAndEliminateLeafMessage]
  with MergingProduction[MergeAndEliminateLeafMessage] {

  override def emit(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateLeafMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateLeafMessage(
      src.mA,
      src.mB
    ))
    case MIDDLE_CHILD => Some(MergeAndEliminateLeafMessage(
      src.mA.transformedBy(1 to 3, 1 to 3)(moveToDest(1, 1))(),
      src.mB.transformedBy(1 to 3, 1 to ctx.mc.mesh.yDofs)(moveToDest(1, 0))()
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateLeafMessage(
      src.mA.transformedBy(1 to 3, 1 to 3)(moveToDest(2, 2))(),
      src.mB.transformedBy(1 to 3, 1 to ctx.mc.mesh.yDofs)(moveToDest(2, 0))()
    ))
  }

  override def merge(a: MergeAndEliminateLeafMessage, b: MergeAndEliminateLeafMessage): MergeAndEliminateLeafMessage = MergeAndEliminateLeafMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: BoundElement, msg: MergeAndEliminateLeafMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    swapDofs(1, 3, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(2, 3, 5, ctx.mc.mesh.yDofs)(dst)

    partialForwardElimination(1, 5, ctx.mc.mesh.yDofs)(dst)
  }

}