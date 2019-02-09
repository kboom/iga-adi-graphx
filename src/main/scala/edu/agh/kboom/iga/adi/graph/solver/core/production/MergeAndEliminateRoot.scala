package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex.childPositionOf
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

sealed case class MergeAndEliminateRootMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateRoot
}

case object MergeAndEliminateRoot extends Production
  with BaseProduction[MergeAndEliminateRootMessage]
  with MergingProduction[MergeAndEliminateRootMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateRootMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateRootMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (0 until 4, 0 until 4) += src.mA(2 until 6, 2 until 6)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (0 until 4, ::) += src.mB(2 until 6, ::)
      }
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateRootMessage(
      MatrixFactory.ofDim(src.mA) {
        _ (2 until 6, 2 until 6) += src.mA(2 until 6, 2 until 6)
      },
      MatrixFactory.ofDim(src.mB) {
        _ (2 until 6, ::) += src.mB(2 until 6, ::)
      }
    ))
  }

  override def merge(a: MergeAndEliminateRootMessage, b: MergeAndEliminateRootMessage): MergeAndEliminateRootMessage = MergeAndEliminateRootMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    partialForwardElimination(6, 6)(dst)
    partialBackwardsSubstitution(6, 6)(dst)
  }
}
