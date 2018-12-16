package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.move
import edu.agh.kboom.core.tree.Vertex.childPositionOf
import edu.agh.kboom.core.tree._
import edu.agh.kboom.core.{MatrixA, MatrixB, IgaTaskContext}

case class MergeAndEliminateLeafMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateLeaf
}

/**
  * M2_3 + E2_1_5
  */
case object MergeAndEliminateLeaf extends Production
  with BaseProduction[MergeAndEliminateLeafMessage]
  with MergingProduction[MergeAndEliminateLeafMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateLeafMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateLeafMessage(
      src.mA,
      src.mB
    ))
    case MIDDLE_CHILD => Some(MergeAndEliminateLeafMessage(
      src.mA.transformedBy(0 until 3, 0 until 3)()(move(1, 1)),
      src.mB.transformedBy(0 until 3, 0 until ctx.mc.mesh.yDofs)()(move(1, 0))
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateLeafMessage(
      src.mA.transformedBy(0 until 3, 0 until 3)()(move(2, 2)),
      src.mB.transformedBy(0 until 3, 0 until ctx.mc.mesh.yDofs)()(move(2, 0))
    ))
  }

  override def merge(a: MergeAndEliminateLeafMessage, b: MergeAndEliminateLeafMessage): MergeAndEliminateLeafMessage = MergeAndEliminateLeafMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateLeafMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA.add(msg.ca)
    dst.mB.add(msg.cb)

    swapDofs(0, 2, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(1, 2, 5, ctx.mc.mesh.yDofs)(dst)

    partialForwardElimination(1, 5, ctx.mc.mesh.yDofs)(dst)
  }

}