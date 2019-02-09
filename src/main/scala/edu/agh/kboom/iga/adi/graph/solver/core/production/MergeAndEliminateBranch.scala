package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex.childPositionOf
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

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
      MatrixFactory.ofDim(src.mA) {
        _ (0 until 4, 0 until 4) += src.mA(1 until 5, 1 until 5)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (0 until 4, ::) += src.mB(1 until 5, ::)
      }
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateBranchMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (2 until 6, 2 until 6) += src.mA(1 until 5, 1 until 5)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (2 until 6, ::) += src.mB(1 until 5, ::)
      }
    ))
  }

  override def merge(a: MergeAndEliminateBranchMessage, b: MergeAndEliminateBranchMessage):
  MergeAndEliminateBranchMessage = MergeAndEliminateBranchMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateBranchMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    swapDofs(0, 2, 6)(dst)
    swapDofs(1, 3, 6)(dst)

    partialForwardElimination(2, 6)(dst)
  }
}
