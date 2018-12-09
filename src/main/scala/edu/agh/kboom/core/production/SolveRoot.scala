package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.{moveFromSource, move}
import edu.agh.kboom.core.{MatrixA, MatrixB, IgaTaskContext}
import edu.agh.kboom.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD}
import edu.agh.kboom.core.tree.Vertex.childPositionOf

sealed case class SolveRootMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateInterim
}

case object SolveRoot extends Production
  with BaseProduction[SolveRootMessage]
  with MergingProduction[SolveRootMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[SolveRootMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(SolveRootMessage(
      src.mA.transformedBy(0 until 4, 0 until 4)(moveFromSource(2, 2))(),
      src.mB.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(moveFromSource(2, 0))()
    ))
    case RIGHT_CHILD => Some(SolveRootMessage(
      src.mA.transformedBy(0 until 4, 0 until 4)(moveFromSource(2, 2), move(2, 2))(),
      src.mB.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(moveFromSource(2, 0), move(2, 0))()
    ))
  }

  override def merge(a: SolveRootMessage, b: SolveRootMessage): SolveRootMessage = SolveRootMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: IgaElement, msg: SolveRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA.add(msg.ca)
    dst.mB.add(msg.cb)

    partialForwardElimination(6, 6, ctx.mc.mesh.yDofs)(dst)
    partialBackwardsSubstitution(6, 6, ctx.mc.mesh.yDofs)(dst)
  }
}
