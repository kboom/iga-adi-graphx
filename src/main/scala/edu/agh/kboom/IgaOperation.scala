package edu.agh.kboom

import edu.agh.kboom.production._
import edu.agh.kboom.tree._

case class IgaOperation(production: Production) {

}

object IgaOperation {

  def operationFor(srcV: Vertex, dstV: Vertex): Option[IgaOperation] = (srcV, dstV) match {
    // up
    case (LeafVertex(_), BranchVertex(_)) => Some(IgaOperation(MergeAndEliminateLeaf()))
    case (BranchVertex(_), InterimVertex(_)) => Some(IgaOperation(MergeAndEliminateBranch()))
    case (InterimVertex(_), RootVertex()) => Some(IgaOperation(MergeAndEliminateRoot()))

    // shared
    case (InterimVertex(a), InterimVertex(b)) => if (a < b) Some(IgaOperation(BackwardsSubstituteInterim())) else Some(IgaOperation(MergeAndEliminateInterim()))

    // down
    case (RootVertex(), InterimVertex(_)) => Some(IgaOperation(BackwardsSubstituteRoot()))
    case (InterimVertex(_), BranchVertex(_)) => Some(IgaOperation(BackwardsSubstituteBranch()))
    case (BranchVertex(_), LeafVertex(_)) => Some(IgaOperation(BackwardsSubstituteLeaves()))
    case _ => None
  }

}
