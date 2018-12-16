package edu.agh.kboom.core.production

import edu.agh.kboom.core.Array2D.{moveFromSource, move}
import edu.agh.kboom.core.{MatrixA, MatrixB, IgaTaskContext}
import edu.agh.kboom.core.tree.Vertex.childPositionOf
import edu.agh.kboom.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD}

sealed case class MergeAndEliminateBranchMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateBranch
}

/**
  * M2_2 + E2_2_6
  */
case object MergeAndEliminateBranch extends Production
  with BaseProduction[MergeAndEliminateBranchMessage]
  with MergingProduction[MergeAndEliminateBranchMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext):
    Option[MergeAndEliminateBranchMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateBranchMessage(
      src.mA.transformedBy(0 until 4, 0 until 4)(move(1, 1))(),
      src.mB.transformedBy(0 until 4, 0 until 4)(move(1, 0))()
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateBranchMessage(
      src.mA.transformedBy(0 until 4, 0 until 4)(move(1, 1))(move(2, 2)),
      src.mB.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(1, 0))(move(2, 0))
    ))
  }

  override def merge(a: MergeAndEliminateBranchMessage, b: MergeAndEliminateBranchMessage):
    MergeAndEliminateBranchMessage = MergeAndEliminateBranchMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateBranchMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA.add(msg.ca)
    dst.mB.add(msg.cb)

    swapDofs(0, 2, 6, ctx.mc.mesh.yDofs)(dst)
    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(dst)

    partialForwardElimination(2, 6, ctx.mc.mesh.yDofs)(dst)
  }
}
