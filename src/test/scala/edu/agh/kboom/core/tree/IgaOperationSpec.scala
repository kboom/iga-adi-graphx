package edu.agh.kboom.core.tree

import edu.agh.kboom.core.IgaOperation
import edu.agh.kboom.core.production._
import org.scalatest.{FunSpec, Matchers}

class IgaOperationSpec extends FunSpec with Matchers {

  it("LeafVertex(16) -> BranchVertex(8) = MergeAndEliminateLeaves") {
    IgaOperation.operationFor(LeafVertex(16), BranchVertex(8)) shouldBe Some(IgaOperation(LeafVertex(16), BranchVertex(8), MergeAndEliminateLeaf))
  }

  it("BranchVertex(8) -> InterimVertex(4) = MergeAndEliminateBranch") {
    IgaOperation.operationFor(BranchVertex(8), InterimVertex(4)) shouldBe Some(IgaOperation(BranchVertex(8), InterimVertex(4), MergeAndEliminateInterim))
  }

  it("InterimVertex(4) -> InterimVertex(2) = MergeAndEliminateInterim") {
    IgaOperation.operationFor(InterimVertex(4), InterimVertex(2)) shouldBe Some(IgaOperation(InterimVertex(4), InterimVertex(2), MergeAndEliminateInterim))
  }

  it("InterimVertex(2) -> RootVertex = MergeAndEliminateRoot") {
    IgaOperation.operationFor(InterimVertex(2), RootVertex()) shouldBe Some(IgaOperation(InterimVertex(2), RootVertex(), SolveRoot))
  }

  it("RootVertex -> InterimVertex(2) = BackwardsSubstituteRoot") {
    IgaOperation.operationFor(RootVertex(), InterimVertex(2)) shouldBe Some(IgaOperation(RootVertex(), InterimVertex(2), BackwardsSubstituteInterim))
  }

  it("InterimVertex(2) -> InterimVertex(4) = BackwardsSubstituteIntermediate") {
    IgaOperation.operationFor(InterimVertex(2), InterimVertex(4)) shouldBe Some(IgaOperation(InterimVertex(2), InterimVertex(4), BackwardsSubstituteBranch))
  }

  it("InterimVertex(4) -> BranchVertex(8) = BackwardsSubstituteBranch") {
    IgaOperation.operationFor(InterimVertex(4), BranchVertex(8)) shouldBe Some(IgaOperation(InterimVertex(4), BranchVertex(8), BackwardsSubstituteBranch))
  }

}