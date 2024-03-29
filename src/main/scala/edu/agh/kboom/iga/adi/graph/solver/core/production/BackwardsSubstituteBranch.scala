package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD, Vertex}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

import scala.annotation.switch

sealed case class BackwardsSubstituteBranchMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteBranch
}

/**
  * BS_2_6 + BS_1_5
  */
case object BackwardsSubstituteBranch extends Production
  with BaseProduction[BackwardsSubstituteBranchMessage] {

  private val r1u5 = 1 until 5
  private val r0u4 = 0 until 4
  private val r2u6 = 2 until 6

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): BackwardsSubstituteBranchMessage = {
    val copiedSource = IgaElement.copy(src)
    partialBackwardsSubstitution(2, 6)(copiedSource)
    swapDofs(0, 2, 6)(copiedSource)
    swapDofs(1, 3, 6)(copiedSource)

    (Vertex.childPositionOf(dst.v)(ctx.tree): @switch) match {
      case LEFT_CHILD => BackwardsSubstituteBranchMessage(
        MatrixFactory.ofDim(copiedSource.mX) {
          _ (r1u5, ::) += copiedSource.mX(r0u4, ::)
        }
      )
      case RIGHT_CHILD => BackwardsSubstituteBranchMessage(
        MatrixFactory.ofDim(copiedSource.mX) {
          _ (r1u5, ::) += copiedSource.mX(r2u6, ::)
        }
      )
    }
  }

  override def consume(dst: IgaElement, msg: BackwardsSubstituteBranchMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX :+= msg.cx
    partialBackwardsSubstitution(1, 5)(dst)
    swapDofs(0, 1, 5)(dst)
    swapDofs(1, 2, 5)(dst)
  }

}
