package edu.agh.kboom.production
import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.core.Array2D.{moveToDest, moveFromSource, moveDown}
import edu.agh.kboom.core.{ArrayA, ArrayB, ArrayX}
import edu.agh.kboom.tree.Vertex.childPositionOf
import edu.agh.kboom.tree.{BoundElement, LEFT_CHILD, RIGHT_CHILD}

sealed case class MergeAndEliminateUpperBranchMessage(ca: ArrayA, cb: ArrayB) extends ProductionMessage {
  override val production: Production = MergeAndEliminateUpperBranch()
}

//  for (int i = 1; i <= 4; i++) {
//            for (int j = 1; j <= 4; j++) {
//                T.m_a[i][j] += T.leftChild.m_a[i + 1][j + 1];
//                T.m_a[i + 2][j + 2] += T.rightChild.m_a[i + 1][j + 1];
//            }
//            for (int j = 1; j <= T.mesh.getDofsY(); j++) {
//                T.m_b[i][j] += T.leftChild.m_b[i + 1][j];
//                T.m_b[i + 2][j] += T.rightChild.m_b[i + 1][j];
//            }
//        }
//        swapDofs(1, 3, 6, T.mesh.getDofsY());
//        swapDofs(2, 4, 6, T.mesh.getDofsY());
//        return T;

/**
  * M2_2
  */
case class MergeAndEliminateUpperBranch() extends Production
  with BaseProduction[MergeAndEliminateUpperBranchMessage]
  with MergingProduction[MergeAndEliminateUpperBranchMessage] {

  override def send(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[MergeAndEliminateUpperBranchMessage] = childPositionOf(src.v)(ctx.tree) match {
    case LEFT_CHILD => Some(MergeAndEliminateUpperBranchMessage(
      src.mA.transformedBy(1 to 4, 1 to 4)(moveFromSource(2, 2))(),
      src.mB.transformedBy(1 to 4, 1 to 4)(moveFromSource(2, 0))()
    ))
    case RIGHT_CHILD => Some(MergeAndEliminateUpperBranchMessage(
      src.mA.transformedBy(1 to 4, 1 to 4)(moveFromSource(2, 2), moveToDest(2, 2))(),
      src.mB.transformedBy(1 to 4, 1 to ctx.mc.mesh.yDofs)(moveFromSource(2, 0), moveToDest(2, 0))()
    ))
  }

  override def merge(a: MergeAndEliminateUpperBranchMessage, b: MergeAndEliminateUpperBranchMessage): MergeAndEliminateUpperBranchMessage = MergeAndEliminateUpperBranchMessage(
    a.ca + b.ca,
    a.cb + b.cb
  )

  override def receive(dst: BoundElement, msg: MergeAndEliminateUpperBranchMessage)(implicit ctx: IgaTaskContext): Unit = {
    dst.mA += msg.ca
    dst.mB += msg.cb

    swapDofs(1, 3, 6, ctx.mc.mesh.yDofs)(dst)
    swapDofs(2, 4, 6, ctx.mc.mesh.yDofs)(dst)

    partialForwardElimination(2, 6, ctx.mc.mesh.yDofs)(dst)
  }
}
