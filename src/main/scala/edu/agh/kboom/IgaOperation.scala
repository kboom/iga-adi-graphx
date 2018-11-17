package edu.agh.kboom

import edu.agh.kboom.production._
import edu.agh.kboom.tree._

case class IgaOperation(src: Vertex, dst: Vertex, p: Production)

object IgaOperation {

  def operationFor(srcV: Vertex, dstV: Vertex): Option[IgaOperation] = (srcV, dstV) match {
    // up
    case (LeafVertex(_), LowerBranchVertex(_)) => Some(IgaOperation(srcV, dstV, MergeAndEliminateLeaf()))
    case (LowerBranchVertex(_), UpperBranchVertex(_)) => Some(IgaOperation(srcV, dstV, MergeAndEliminateLowerBranch()))
    case (UpperBranchVertex(_), InterimVertex(_)) => Some(IgaOperation(srcV, dstV, MergeAndEliminateLowerBranch()))
    case (InterimVertex(_), RootVertex()) => Some(IgaOperation(srcV, dstV, SolveRoot()))

    // shared
    case (InterimVertex(a), InterimVertex(b)) => if (a < b) Some(IgaOperation(srcV, dstV, BackwardsSubstituteInterim())) else Some(IgaOperation(srcV, dstV, MergeAndEliminateInterim()))

    // down
    case (RootVertex(), InterimVertex(_)) => Some(IgaOperation(srcV, dstV, BackwardsSubstituteRoot()))
    case (InterimVertex(_), LowerBranchVertex(_)) => Some(IgaOperation(srcV, dstV, BackwardsSubstituteBranch()))
    case (LowerBranchVertex(_), LeafVertex(_)) => Some(IgaOperation(srcV, dstV, BackwardsSubstituteLeaves()))
    case _ => None
  }

}
