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

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateInterimMessage] = (childPositionOf(src.v)(ctx.tree): @switch) match {
    case LEFT_CHILD => Some(MergeAndEliminateInterimMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (0 until 4, 0 until 4) += src.mA(2 until 6, 2 until 6)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (0 until 4, ::) += src.mB(2 until 6, ::)
      }
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateInterimMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (2 until 6, 2 until 6) += src.mA(2 until 6, 2 until 6)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (2 until 6, ::) += src.mB(2 until 6, ::)
      }
    ))
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
