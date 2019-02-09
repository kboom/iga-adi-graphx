package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD, Vertex}
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, MatrixX}
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixUtil.DenseMatrixUtil

sealed case class BackwardsSubstituteInterimMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteInterim
}

case object BackwardsSubstituteInterim extends Production
  with BaseProduction[BackwardsSubstituteInterimMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteInterimMessage] = {
    val copiedElement = IgaElement.copy(src)

    partialBackwardsSubstitution(2, 6)(copiedElement)
    swapDofs(0, 2, 6)(copiedElement)
    swapDofs(1, 3, 6)(copiedElement)

    Vertex.childPositionOf(dst.v)(ctx.tree) match {
      case LEFT_CHILD => Some(BackwardsSubstituteInterimMessage(
        MatrixX.ofDim(copiedElement.mX)(2 to -1, ::) ::+ copiedElement.mX(0 until 4, ::)
      ))
      case RIGHT_CHILD => Some(BackwardsSubstituteInterimMessage(
        MatrixX.ofDim(copiedElement.mX)(2 to -1, ::) ::+ copiedElement.mX(2 until 6, ::)
      ))
    }
  }

  override def consume(dst: IgaElement, msg: BackwardsSubstituteInterimMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX :+= msg.cx
  }

}
