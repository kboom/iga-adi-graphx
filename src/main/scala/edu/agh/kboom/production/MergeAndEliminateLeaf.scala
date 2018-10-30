package edu.agh.kboom.production

import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.core.Array2D.{move, moveDown}
import edu.agh.kboom.core.{ArrayA, ArrayB}
import edu.agh.kboom.tree.Vertex.childPositionOf
import edu.agh.kboom.tree._

case class MergeAndEliminateLeafMessage(production: MergeAndEliminateLeaf, ca: ArrayA, cb: ArrayB)
  extends ProductionMessage[MergeAndEliminateLeaf]


/*
for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                T.m_a[i][j] += T.leftChild.m_a[i][j];
                T.m_a[i + 1][j + 1] += T.middleChild.m_a[i][j];
                T.m_a[i + 2][j + 2] += T.rightChild.m_a[i][j];
            }
            for (int j = 1; j <= T.mesh.getDofsY(); j++) {
                T.m_b[i][j] += T.leftChild.m_b[i][j];
                T.m_b[i + 1][j] += T.middleChild.m_b[i][j];
                T.m_b[i + 2][j] += T.rightChild.m_b[i][j];
            }
        }
 */
case class MergeAndEliminateLeaf() extends Production[MergeAndEliminateLeafMessage] {

  override def send(src: BoundElement)(implicit ctx: IgaTaskContext): Seq[MergeAndEliminateLeafMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Seq(MergeAndEliminateLeafMessage(
      this,
      src.mA,
      src.mB
    ))
    case MIDDLE_CHILD => Seq(MergeAndEliminateLeafMessage(
      this,
      src.mA.transformedBy(1 to 3, 1 to 3)(move(1, 1))(),
      src.mB.transformedBy(1 to 3, 1 to ctx.mc.mesh.yDofs)(moveDown(1))()
    ))
    case RIGHT_CHILD => Seq(MergeAndEliminateLeafMessage(
      this,
      src.mA.transformedBy(1 to 3, 1 to 3)(move(2, 2))(),
      src.mB.transformedBy(1 to 3, 1 to ctx.mc.mesh.yDofs)(moveDown(2))()
    ))
  }

  override def merge(a: MergeAndEliminateLeafMessage, b: MergeAndEliminateLeafMessage)(implicit ctx: IgaTaskContext): MergeAndEliminateLeafMessage = MergeAndEliminateLeafMessage(
    this,
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def receive(dst: BoundElement, msg: MergeAndEliminateLeafMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    swapDofs(1, 3, 5, ctx.mc.mesh.yDofs)(dst)
    swapDofs(2, 3, 5, ctx.mc.mesh.yDofs)(dst)
  }

}