package edu.agh.kboom.production

import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.core.Array2D.{moveFromSource, moveToDest}
import edu.agh.kboom.core.{ArrayA, ArrayB}
import edu.agh.kboom.tree.{BoundElement, LEFT_CHILD, RIGHT_CHILD}
import edu.agh.kboom.tree.Vertex.childPositionOf

sealed case class SolveRootMessage(ca: ArrayA, cb: ArrayB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateInterim()
}

case class SolveRoot() extends Production
  with BaseProduction[SolveRootMessage]
  with MergingProduction[SolveRootMessage] {

  override def send(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[SolveRootMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(SolveRootMessage(
      src.mA.transformedBy(1 to 4, 1 to 4)(moveFromSource(2, 2))(),
      src.mB.transformedBy(1 to 4, 1 to 4)(moveFromSource(2, 0))()
    ))
    case RIGHT_CHILD => Some(SolveRootMessage(
      src.mA.transformedBy(1 to 4, 1 to 4)(moveFromSource(2, 2), moveToDest(2, 2))(),
      src.mB.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)(moveFromSource(2, 0), moveToDest(2, 0))()
    ))
  }

  override def merge(a: SolveRootMessage, b: SolveRootMessage): SolveRootMessage = SolveRootMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def receive(dst: BoundElement, msg: SolveRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    partialForwardElimination(6, 6, ctx.mc.mesh.yDofs)(dst)
    partialBackwardsSubstitution(6, 6, ctx.mc.mesh.yDofs)(dst)
  }
}
