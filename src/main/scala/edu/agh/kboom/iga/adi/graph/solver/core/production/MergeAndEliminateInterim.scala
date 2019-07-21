package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex.childPositionOf
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

import scala.annotation.switch

sealed case class MergeAndEliminateInterimMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateInterim
}

case object MergeAndEliminateInterim extends Production
  with BaseProduction[MergeAndEliminateInterimMessage]
  with MergingProduction[MergeAndEliminateInterimMessage] {

  private val r0u4 = 0 until 4
  private val r2u6 = 2 until 6

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): MergeAndEliminateInterimMessage = (childPositionOf(src.v)(ctx.tree): @switch) match {
    case LEFT_CHILD => MergeAndEliminateInterimMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (r0u4, r0u4) += src.mA(r2u6, r2u6)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (r0u4, ::) += src.mB(r2u6, ::)
      }
    )
    case RIGHT_CHILD => MergeAndEliminateInterimMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (r2u6, r2u6) += src.mA(r2u6, r2u6)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (r2u6, ::) += src.mB(r2u6, ::)
      }
    )
  }

  override def merge(a: MergeAndEliminateInterimMessage, b: MergeAndEliminateInterimMessage): MergeAndEliminateInterimMessage = MergeAndEliminateInterimMessage(
    a.ca += b.ca,
    a.cb += b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateInterimMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    swapDofs(0, 2, 6)(dst)
    swapDofs(1, 3, 6)(dst)

    partialForwardElimination(2, 6)(dst)
  }
}
