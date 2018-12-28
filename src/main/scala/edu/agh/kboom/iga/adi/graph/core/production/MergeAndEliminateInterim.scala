package edu.agh.kboom.iga.adi.graph.core.production

import edu.agh.kboom.iga.adi.graph.core.Array2D.{moveFromSource, move}
import edu.agh.kboom.iga.adi.graph.core.{MatrixA, MatrixB, IgaTaskContext}
import edu.agh.kboom.iga.adi.graph.core.tree.Vertex.childPositionOf
import edu.agh.kboom.iga.adi.graph.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD}

sealed case class MergeAndEliminateInterimMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateInterim
}

case object MergeAndEliminateInterim extends Production
  with BaseProduction[MergeAndEliminateInterimMessage]
  with MergingProduction[MergeAndEliminateInterimMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateInterimMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateInterimMessage(
      src.mA.transformedBy(0 until 4, 0 until 4)(move(2, 2))(),
      src.mB.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(2, 0))()
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateInterimMessage(
      src.mA.transformedBy(0 until 4, 0 until 4)(move(2, 2))(move(2, 2)),
      src.mB.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(2, 0))(move(2, 0))
    ))
  }

  override def merge(a: MergeAndEliminateInterimMessage, b: MergeAndEliminateInterimMessage): MergeAndEliminateInterimMessage = MergeAndEliminateInterimMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateInterimMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA.add(msg.ca)
    dst.mB.add(msg.cb)

    swapDofs(0, 2, 6, ctx.mc.mesh.yDofs)(dst)
    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(dst)

    partialForwardElimination(2, 6, ctx.mc.mesh.yDofs)(dst)
  }
}
