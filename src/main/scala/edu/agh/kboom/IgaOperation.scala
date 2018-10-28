package edu.agh.kboom

import edu.agh.kboom.production._
import edu.agh.kboom.tree._

case class IgaOperation(production: Production) {

}

object IgaOperation {

  def operationFor(srcV: Vertex, dstV: Vertex): IgaOperation = (srcV, dstV) match {
    // up
    case (LeafVertex(_), BranchVertex(_)) => IgaOperation(MergeAndEliminateLeaf())
    case (BranchVertex(_), InterimVertex(_)) => IgaOperation(MergeAndEliminateBranch())
    case (InterimVertex(_), InterimVertex(_)) => IgaOperation(MergeAndEliminateInterim())
    case (InterimVertex(_), RootVertex()) => IgaOperation(MergeAndEliminateRoot())

    // shared
    case (InterimVertex(a), InterimVertex(b)) => if (a < b) IgaOperation(BackwardsSubstituteIntermediate()) else IgaOperation(MergeAndEliminateInterim())

    // down
    case (RootVertex(), InterimVertex(_)) => IgaOperation(BackwardsSubstituteRoot())
    case (InterimVertex(_), BranchVertex(_)) => IgaOperation(BackwardsSubstituteBranch())
    case (BranchVertex(_), LeafVertex(_)) => IgaOperation(BackwardsSubstituteLeaves())
  }

}
