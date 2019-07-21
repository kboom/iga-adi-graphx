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

  private val r0u3 = 0 until 3
  private val r1u4 = 1 until 4
  private val r2u5 = 2 until 5

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): MergeAndEliminateLeafMessage = (childPositionOf(src.v)(ctx.tree): @switch) match {
    case LEFT_CHILD => MergeAndEliminateLeafMessage(
      ofDim(src.mA) {
        _ (r0u3, r0u3) += src.mA(r0u3, r0u3)
      },
      ofDim(src.mB) {
        _ (r0u3, ::) += src.mB(r0u3, ::)
      }
    )
    case MIDDLE_CHILD => MergeAndEliminateLeafMessage(
      ofDim(src.mA) {
        _ (r1u4, r1u4) += src.mA(r0u3, r0u3)
      },
      ofDim(src.mB) {
        _ (r1u4, ::) += src.mB(r0u3, ::)
      }
    )
    case RIGHT_CHILD => MergeAndEliminateLeafMessage(
      ofDim(src.mA) {
        _ (r2u5, r2u5) += src.mA(r0u3, r0u3)
      },
      ofDim(src.mB) {
        _ (r2u5, ::) += src.mB(r0u3, ::)
      }
    )
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