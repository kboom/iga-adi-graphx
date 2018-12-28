package edu.agh.kboom.iga.adi.graph.core

import edu.agh.kboom.iga.adi.graph.core.production._
import edu.agh.kboom.iga.adi.graph.core.tree._

case class IgaOperation(src: Vertex, dst: Vertex, p: Production)

object IgaOperation {

  def operationFor(srcV: Vertex, dstV: Vertex): Option[IgaOperation] = (srcV, dstV) match {
    // up
    case (LeafVertex(_), BranchVertex(_)) => Some(IgaOperation(srcV, dstV, MergeAndEliminateLeaf))
    case (BranchVertex(_), InterimVertex(_)) => Some(IgaOperation(srcV, dstV, MergeAndEliminateBranch))
    case (InterimVertex(_), RootVertex()) => Some(IgaOperation(srcV, dstV, MergeAndEliminateRoot))

    // shared
    case (InterimVertex(a), InterimVertex(b)) => if (a > b) Some(IgaOperation(srcV, dstV, MergeAndEliminateInterim))
      else Some(IgaOperation(srcV, dstV, BackwardsSubstituteInterim))

    // down
    case (RootVertex(), InterimVertex(_)) => Some(IgaOperation(srcV, dstV, BackwardsSubstituteRoot))
    case (InterimVertex(_), BranchVertex(_)) => Some(IgaOperation(srcV, dstV, BackwardsSubstituteBranch))
    case _ => None
  }

}
