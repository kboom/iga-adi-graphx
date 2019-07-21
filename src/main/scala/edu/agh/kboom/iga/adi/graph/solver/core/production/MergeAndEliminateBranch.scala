package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex.childPositionOf
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

import scala.annotation.switch

sealed case class MergeAndEliminateBranchMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateBranch
}

/**
  * M2_2 + E2_2_6
  */
case object MergeAndEliminateBranch extends Production
  with BaseProduction[MergeAndEliminateBranchMessage]
  with MergingProduction[MergeAndEliminateBranchMessage] {

  private val r1t5 = 1 until 5
  private val r0u4 = 0 until 4
  private val r0u6 = 2 until 6

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): MergeAndEliminateBranchMessage = (childPositionOf(src.v)(ctx.tree): @switch) match {
    case LEFT_CHILD => MergeAndEliminateBranchMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (r0u4, r0u4) += src.mA(r1t5, r1t5)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (r0u4, ::) += src.mB(r1t5, ::)
      }
    )
    case RIGHT_CHILD => MergeAndEliminateBranchMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (r0u6, r0u6) += src.mA(r1t5, r1t5)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (r0u6, ::) += src.mB(r1t5, ::)
      }
    )
  }

  override def merge(a: MergeAndEliminateBranchMessage, b: MergeAndEliminateBranchMessage):
  MergeAndEliminateBranchMessage = MergeAndEliminateBranchMessage(
    a.ca += b.ca,
    a.cb += b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateBranchMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    swapDofs(0, 2, 6)(dst)
    swapDofs(1, 3, 6)(dst)

    partialForwardElimination(2, 6)(dst)
  }
}
