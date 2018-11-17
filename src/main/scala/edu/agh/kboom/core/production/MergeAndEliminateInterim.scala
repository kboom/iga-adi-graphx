package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.{moveFromSource, moveToDest}
import edu.agh.kboom.core.{ArrayA, ArrayB, IgaTaskContext}
import edu.agh.kboom.core.tree.Vertex.childPositionOf
import edu.agh.kboom.core.tree.{BoundElement, LEFT_CHILD, RIGHT_CHILD}

sealed case class MergeAndEliminateInterimMessage(ca: ArrayA, cb: ArrayB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateInterim
}

case object MergeAndEliminateInterim extends Production
  with BaseProduction[MergeAndEliminateInterimMessage]
  with MergingProduction[MergeAndEliminateInterimMessage] {

  override def emit(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateInterimMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateInterimMessage(
      src.mA.transformedBy(1 to 4, 1 to 4)(moveFromSource(2, 2))(),
      src.mB.transformedBy(1 to 4, 1 to 4)(moveFromSource(2, 0))()
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateInterimMessage(
      src.mA.transformedBy(1 to 4, 1 to 4)(moveFromSource(2, 2), moveToDest(2, 2))(),
      src.mB.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)(moveFromSource(2, 0), moveToDest(2, 0))()
    ))
  }

  override def merge(a: MergeAndEliminateInterimMessage, b: MergeAndEliminateInterimMessage): MergeAndEliminateInterimMessage = MergeAndEliminateInterimMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: BoundElement, msg: MergeAndEliminateInterimMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(dst)
    swapDofs(2, 4, 6, ctx.mc.mesh.yDofs)(dst)

    partialForwardElimination(2, 6, ctx.mc.mesh.yDofs)(dst)
  }
}
