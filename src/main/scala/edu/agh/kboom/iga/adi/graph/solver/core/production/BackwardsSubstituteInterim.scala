package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD, Vertex}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixFactory}

import scala.annotation.switch

sealed case class BackwardsSubstituteInterimMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteInterim
}

case object BackwardsSubstituteInterim extends Production
  with BaseProduction[BackwardsSubstituteInterimMessage] {

  private val r2tom1 = 2 to -1
  private val r0u4 = 0 until 4
  private val r2u6 = 2 until 6

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): BackwardsSubstituteInterimMessage = {
    val copiedElement = IgaElement.copy(src)

    partialBackwardsSubstitution(2, 6)(copiedElement)
    swapDofs(0, 2, 6)(copiedElement)
    swapDofs(1, 3, 6)(copiedElement)

    (Vertex.childPositionOf(dst.v)(ctx.tree): @switch) match {
      case LEFT_CHILD => BackwardsSubstituteInterimMessage(
        MatrixFactory.ofDim(copiedElement.mX) {
          _ (r2tom1, ::) += copiedElement.mX(r0u4, ::)
        }
      )
      case RIGHT_CHILD => BackwardsSubstituteInterimMessage(
        MatrixFactory.ofDim(copiedElement.mX) {
          _ (r2tom1, ::) += copiedElement.mX(r2u6, ::)
        }
      )
    }
  }

  override def consume(dst: IgaElement, msg: BackwardsSubstituteInterimMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX :+= msg.cx
  }

}
