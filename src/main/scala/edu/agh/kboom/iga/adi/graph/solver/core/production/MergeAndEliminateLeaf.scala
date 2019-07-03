package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixFactory.ofDim
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex.childPositionOf
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

import scala.annotation.switch

case class MergeAndEliminateLeafMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateLeaf
}

/**
  * M2_3 + E2_1_5
  */
case object MergeAndEliminateLeaf extends Production
  with BaseProduction[MergeAndEliminateLeafMessage]
  with MergingProduction[MergeAndEliminateLeafMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateLeafMessage] = (childPositionOf(src.v)(ctx.tree): @switch) match {
    case LEFT_CHILD => Some(MergeAndEliminateLeafMessage(
      ofDim(src.mA) {
        _ (0 until 3, 0 until 3) += src.mA(0 until 3, 0 until 3)
      },
      ofDim(src.mB) {
        _ (0 until 3, ::) += src.mB(0 until 3, ::)
      }
    ))
    case MIDDLE_CHILD => Some(MergeAndEliminateLeafMessage(
      ofDim(src.mA) {
        _ (1 until 4, 1 until 4) += src.mA(0 until 3, 0 until 3)
      },
      ofDim(src.mB) {
        _ (1 until 4, ::) += src.mB(0 until 3, ::)
      }
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateLeafMessage(
      ofDim(src.mA) {
        _ (2 until 5, 2 until 5) += src.mA(0 until 3, 0 until 3)
      },
      ofDim(src.mB) {
        _ (2 until 5, ::) += src.mB(0 until 3, ::)
      }
    ))
  }

  override def merge(a: MergeAndEliminateLeafMessage, b: MergeAndEliminateLeafMessage): MergeAndEliminateLeafMessage = MergeAndEliminateLeafMessage(
    a.ca += b.ca,
    a.cb += b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateLeafMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

//    println(IgaElement.print(dst))

    swapDofs(0, 2, 5)(dst)
    swapDofs(1, 2, 5)(dst)

    partialForwardElimination(1, 5)(dst)
  }

}