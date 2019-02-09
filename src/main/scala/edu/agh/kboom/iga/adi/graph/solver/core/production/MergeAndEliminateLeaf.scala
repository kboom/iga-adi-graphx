package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex.childPositionOf
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixA, MatrixB}

case class MergeAndEliminateLeafMessage(ca: MatrixA, cb: MatrixB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateLeaf
}

/**
  * M2_3 + E2_1_5
  */
case object MergeAndEliminateLeaf extends Production
  with BaseProduction[MergeAndEliminateLeafMessage]
  with MergingProduction[MergeAndEliminateLeafMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateLeafMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateLeafMessage(
      src.mA,
      src.mB
    ))
    case MIDDLE_CHILD => Some(MergeAndEliminateLeafMessage(
      MatrixA.ofDim(src.mA)(1 until 4, 1 until 4) :+= src.mA(0 until 3, 0 until 3),
      MatrixB.ofDim(src.mB)(1 until 4, ::) :+= src.mB(0 until 3, ::)
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateLeafMessage(
      MatrixA.ofDim(src.mA)(2 until 5, 2 until 5) :+= src.mA(0 until 3, 0 until 3),
      MatrixB.ofDim(src.mB)(2 until 5, ::) :+= src.mB(0 until 3, ::)
    ))
  }

  override def merge(a: MergeAndEliminateLeafMessage, b: MergeAndEliminateLeafMessage): MergeAndEliminateLeafMessage = MergeAndEliminateLeafMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def consume(dst: IgaElement, msg: MergeAndEliminateLeafMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA :+= msg.ca
    dst.mB :+= msg.cb

    swapDofs(0, 2, 5)(dst)
    swapDofs(1, 2, 5)(dst)

    partialForwardElimination(1, 5)(dst)
  }

}