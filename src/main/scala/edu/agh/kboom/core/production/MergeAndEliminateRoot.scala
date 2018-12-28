package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.move
import edu.agh.kboom.core.tree.Vertex.childPositionOf
import edu.agh.kboom.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD}
import edu.agh.kboom.core.{IgaTaskContext, MatrixA, MatrixB}

sealed case class MergeAndEliminateRootMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateRoot
}

case object MergeAndEliminateRoot extends Production
  with BaseProduction[MergeAndEliminateRootMessage]
  with MergingProduction[MergeAndEliminateRootMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateRootMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateRootMessage(
      src.mA.transformedBy(0 until 4, 0 until 4)(move(2, 2))(),
      src.mB.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(2, 0))()
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateRootMessage(
      src.mA.transformedBy(0 until 4, 0 until 4)(move(2, 2))(move(2, 2)),
      src.mB.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(2, 0))(move(2, 0))
    ))
  }

  override def merge(a: MergeAndEliminateRootMessage, b: MergeAndEliminateRootMessage): MergeAndEliminateRootMessage = MergeAndEliminateRootMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA.add(msg.ca)
    dst.mB.add(msg.cb)

    partialForwardElimination(6, 6, ctx.mc.mesh.yDofs)(dst)
    partialBackwardsSubstitution(6, 6, ctx.mc.mesh.yDofs)(dst)
  }
}
