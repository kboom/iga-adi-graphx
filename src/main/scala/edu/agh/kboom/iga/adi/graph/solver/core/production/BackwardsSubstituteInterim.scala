package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.Array2D.{moveFromSource, move}
import edu.agh.kboom.iga.adi.graph.solver.core.{MatrixX, IgaTaskContext}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD, Vertex}

sealed case class BackwardsSubstituteInterimMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteInterim
}

case object BackwardsSubstituteInterim extends Production
  with BaseProduction[BackwardsSubstituteInterimMessage] {

  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteInterimMessage] = {
    val copiedElement = IgaElement.copy(src)

    partialBackwardsSubstitution(2, 6, ctx.mc.mesh.yDofs)(copiedElement)
    swapDofs(0, 2, 6, ctx.mc.mesh.yDofs)(copiedElement)
    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(copiedElement)

    Vertex.childPositionOf(dst.v)(ctx.tree) match {
      case LEFT_CHILD => Some(BackwardsSubstituteInterimMessage(
        copiedElement.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)()(move(2, 0))
      ))
      case RIGHT_CHILD => Some(BackwardsSubstituteInterimMessage(
        copiedElement.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(2, 0))(move(2, 0))
      ))
    }
  }

  override def consume(dst: IgaElement, msg: BackwardsSubstituteInterimMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX.add(msg.cx)
  }

}