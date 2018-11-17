package edu.agh.kboom.core.tree

import edu.agh.kboom.core.IgaOperation
import edu.agh.kboom.core.production._
import org.scalatest.FunSpec

class IgaOperationSpec extends FunSpec {

  it("LeafVertex(16) -> BranchVertex(8) = MergeAndEliminateLeaves") {
    assert(IgaOperation.operationFor(LeafVertex(16), BranchVertex(8)) == IgaOperation(MergeAndEliminateLeaf()))
  }

  it("BranchVertex(8) -> InterimVertex(4) = MergeAndEliminateBranch") {
    assert(IgaOperation.operationFor(BranchVertex(8), InterimVertex(4)) == IgaOperation(MergeAndEliminateInterim()))
  }

  it("InterimVertex(4) -> InterimVertex(2) = MergeAndEliminateInterim") {
    assert(IgaOperation.operationFor(InterimVertex(4), InterimVertex(2)) == IgaOperation(MergeAndEliminateInterim()))
  }

  it("InterimVertex(2) -> RootVertex = MergeAndEliminateRoot") {
    assert(IgaOperation.operationFor(InterimVertex(2), RootVertex()) == IgaOperation(SolveRoot()))
  }

  it("RootVertex -> InterimVertex(2) = BackwardsSubstituteRoot") {
    assert(IgaOperation.operationFor(RootVertex(), InterimVertex(2)) == IgaOperation(BackwardsSubstituteInterim()))
  }

  it("InterimVertex(2) -> InterimVertex(4) = BackwardsSubstituteIntermediate") {
    assert(IgaOperation.operationFor(InterimVertex(2), InterimVertex(4)) == IgaOperation(BackwardsSubstituteBranch()))
  }

  it("InterimVertex(4) -> BranchVertex(8) = BackwardsSubstituteBranch") {
    assert(IgaOperation.operationFor(InterimVertex(4), BranchVertex(8)) == IgaOperation(BackwardsSubstituteLeaf()))
  }

}
