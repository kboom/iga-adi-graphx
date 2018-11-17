package edu.agh.kboom.production
import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.core.Array2D.{moveToDest, moveFromSource, moveDown}
import edu.agh.kboom.core.{ArrayA, ArrayB, ArrayX}
import edu.agh.kboom.tree.Vertex.childPositionOf
import edu.agh.kboom.tree.{BoundElement, LEFT_CHILD, RIGHT_CHILD}

sealed case class MergeAndEliminateBranchMessage(ca: ArrayA, cb: ArrayB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateBranch()
}

case class MergeAndEliminateBranch() extends Production
  with BaseProduction[MergeAndEliminateBranchMessage]
  with MergingProduction[MergeAndEliminateBranchMessage] {

  override def send(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateBranchMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateBranchMessage(
      src.mA.transformedBy(1 to 4, 1 to 4)(moveFromSource(1, 1))(),
      src.mB.transformedBy(1 to 4, 1 to 4)(moveFromSource(1, 0))()
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateBranchMessage(
      src.mA.transformedBy(1 to 4, 1 to 4)(moveFromSource(1, 1), moveToDest(2, 2))(),
      src.mB.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)(moveFromSource(1, 0), moveToDest(2, 0))()
    ))
  }

  override def merge(a: MergeAndEliminateBranchMessage, b: MergeAndEliminateBranchMessage): MergeAndEliminateBranchMessage = MergeAndEliminateBranchMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def receive(dst: BoundElement, msg: MergeAndEliminateBranchMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(dst)
    swapDofs(2, 4, 6, ctx.mc.mesh.yDofs)(dst)

    partialForwardElimination(2, 6, ctx.mc.mesh.yDofs)(dst)
  }
}
