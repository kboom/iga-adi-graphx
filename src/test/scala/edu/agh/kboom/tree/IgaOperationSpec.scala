package edu.agh.kboom.tree

import edu.agh.kboom.IgaOperation
import edu.agh.kboom.production._
import org.scalatest.FunSpec

class IgaOperationSpec extends FunSpec {

  it("LeafVertex(16) -> BranchVertex(8) = MergeAndEliminateLeaves") {
    assert(IgaOperation.operationFor(LeafVertex(16), LowerBranchVertex(8)) == IgaOperation(MergeAndEliminateLeaf()))
  }

  it("BranchVertex(8) -> InterimVertex(4) = MergeAndEliminateBranch") {
    assert(IgaOperation.operationFor(LowerBranchVertex(8), InterimVertex(4)) == IgaOperation(MergeAndEliminateLowerBranch()))
  }

  it("InterimVertex(4) -> InterimVertex(2) = MergeAndEliminateInterim") {
    assert(IgaOperation.operationFor(InterimVertex(4), InterimVertex(2)) == IgaOperation(MergeAndEliminateInterim()))
  }

  it("InterimVertex(2) -> RootVertex = MergeAndEliminateRoot") {
    assert(IgaOperation.operationFor(InterimVertex(2), RootVertex()) == IgaOperation(MergeAndEliminateRoot()))
  }

  it("RootVertex -> InterimVertex(2) = BackwardsSubstituteRoot") {
    assert(IgaOperation.operationFor(RootVertex(), InterimVertex(2)) == IgaOperation(BackwardsSubstituteRoot()))
  }

  it("InterimVertex(2) -> InterimVertex(4) = BackwardsSubstituteIntermediate") {
    assert(IgaOperation.operationFor(InterimVertex(2), InterimVertex(4)) == IgaOperation(BackwardsSubstituteInterim()))
  }

  it("InterimVertex(4) -> BranchVertex(8) = BackwardsSubstituteBranch") {
    assert(IgaOperation.operationFor(InterimVertex(4), LowerBranchVertex(8)) == IgaOperation(BackwardsSubstituteBranch()))
  }

}
