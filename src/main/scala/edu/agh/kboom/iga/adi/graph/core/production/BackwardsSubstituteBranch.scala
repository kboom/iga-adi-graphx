package edu.agh.kboom.iga.adi.graph.core.production

import edu.agh.kboom.iga.adi.graph.core.Array2D.{move, moveFromSource}
import edu.agh.kboom.iga.adi.graph.core.IgaTaskExecutor.getClass
import edu.agh.kboom.iga.adi.graph.core.{IgaTaskContext, MatrixX}
import edu.agh.kboom.iga.adi.graph.core.tree.{IgaElement, LEFT_CHILD, RIGHT_CHILD, Vertex}
import org.slf4j.LoggerFactory

sealed case class BackwardsSubstituteBranchMessage(cx: MatrixX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteBranch
}

/**
  * BS_2_6 + BS_1_5
  */
case object BackwardsSubstituteBranch extends Production
  with BaseProduction[BackwardsSubstituteBranchMessage] {

  private val Log = LoggerFactory.getLogger(getClass)

  // todo: This can be run multiple times... the state could be modified twice... don't do it here
  override def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteBranchMessage] = {
    val copiedSource = IgaElement.copy(src)
    partialBackwardsSubstitution(2, 6, ctx.mc.mesh.yDofs)(copiedSource)
    swapDofs(0, 2, 6, ctx.mc.mesh.yDofs)(copiedSource)
    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(copiedSource)

    Log.debug(f"v${src.v}\n ${IgaElement.print(copiedSource)}")

    Vertex.childPositionOf(dst.v)(ctx.tree) match {
      case LEFT_CHILD => Some(BackwardsSubstituteBranchMessage(
        copiedSource.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)()(move(1, 0))
      ))
      case RIGHT_CHILD => Some(BackwardsSubstituteBranchMessage(
        copiedSource.mX.transformedBy(0 until 4, 0 until ctx.mc.mesh.yDofs)(move(2, 0))(move(1, 0))
      ))
    }
  }

  override def consume(dst: IgaElement, msg: BackwardsSubstituteBranchMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX.add(msg.cx)
    partialBackwardsSubstitution(1, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(0, 1, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(1, 2, 5, ctx.mc.mesh.yDofs)(dst)
  }

}
