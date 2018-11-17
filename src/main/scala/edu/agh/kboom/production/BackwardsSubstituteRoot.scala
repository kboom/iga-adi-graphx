package edu.agh.kboom.production

import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.core.Array2D.moveDown
import edu.agh.kboom.core.ArrayX
import edu.agh.kboom.tree.{BoundElement, LEFT_CHILD, RIGHT_CHILD, Vertex}

/*
        T = partial_backward_substitution(T, 2, 6, mesh.getDofsY());
        swapDofs(1, 3, 6, T.mesh.getDofsY());
        swapDofs(2, 4, 6, T.mesh.getDofsY());
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= T.mesh.getDofsY(); j++) {
                T.leftChild.m_x[i + 1][j] = T.m_x[i][j];
                T.rightChild.m_x[i + 1][j] = T.m_x[i + 2][j];
            }
        }

        return T;
 */


/**
  * BS_2_6
  */
sealed case class BackwardsSubstituteRootMessage(cx: ArrayX) extends ProductionMessage {
  override val production: Production = BackwardsSubstituteBranch()
}

case object BackwardsSubstituteRoot extends Production
  with BaseProduction[BackwardsSubstituteRootMessage]
  with PreparingProduction {

  override def prepare(src: BoundElement)(implicit ctx: IgaTaskContext): Unit = {

  }

  override def send(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[BackwardsSubstituteRootMessage] = Vertex.childPositionOf(dst.v)(ctx.tree) match {
    case LEFT_CHILD => Some(BackwardsSubstituteRootMessage(
      src.mX.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)()(moveDown(1))
    ))
    case RIGHT_CHILD => Some(BackwardsSubstituteRootMessage(
      src.mX.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)(moveDown(2))(moveDown(1))
    ))
  }

  override def receive(dst: BoundElement, msg: BackwardsSubstituteRootMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mX += msg.cx
  }

}
