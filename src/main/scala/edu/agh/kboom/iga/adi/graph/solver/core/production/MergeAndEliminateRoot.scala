package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex.childPositionOf
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

import scala.annotation.switch

sealed case class MergeAndEliminateRootMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateRoot
}

case object MergeAndEliminateRoot extends Production
  with BaseProduction[MergeAndEliminateRootMessage]
  with MergingProduction[MergeAndEliminateRootMessage] {

  private val r0u4 = 0 until 4
  private val r2u6 = 2 until 6

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): MergeAndEliminateRootMessage = (childPositionOf(src.v)(ctx.tree): @switch) match {
    case LEFT_CHILD => MergeAndEliminateRootMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (r0u4, r0u4) += src.mA(r2u6, r2u6)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (r0u4, ::) += src.mB(r2u6, ::)
      }
    )
    case RIGHT_CHILD => MergeAndEliminateRootMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (r2u6, r2u6) += src.mA(r2u6, r2u6)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (r2u6, ::) += src.mB(r2u6, ::)
      }
    )
  }

  override def merge(a: MergeAndEliminateRootMessage, b: MergeAndEliminateRootMessage): MergeAndEliminateRootMessage = MergeAndEliminateRootMessage(
    a.ca += b.ca,
    a.cb += b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    partialForwardElimination(6, 6)(dst)
    partialBackwardsSubstitution(6, 6)(dst)
  }
}
