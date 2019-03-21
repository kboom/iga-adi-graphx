package edu.agh.kboom.iga.adi.graph.solver.core.tree

import edu.agh.kboom.iga.adi.graph.solver.core.IgaOperation
import edu.agh.kboom.iga.adi.graph.solver.core.production._
import org.scalatest.{FunSpec, Matchers}

class IgaOperationSpec extends FunSpec with Matchers {

  it("LeafVertex(16) -> BranchVertex(8) = MergeAndEliminateLeaves") {
    IgaOperation.operationFor(LeafVertex(16L), BranchVertex(8L)) shouldBe Some(IgaOperation(LeafVertex(16L), BranchVertex(8L), MergeAndEliminateLeaf))
  }

  it("BranchVertex(8) -> InterimVertex(4) = MergeAndEliminateBranch") {
    IgaOperation.operationFor(BranchVertex(8L), InterimVertex(4L)) shouldBe Some(IgaOperation(BranchVertex(8L), InterimVertex(4L), MergeAndEliminateBranch))
  }

  it("InterimVertex(4) -> InterimVertex(2) = MergeAndEliminateInterim") {
    IgaOperation.operationFor(InterimVertex(4L), InterimVertex(2L)) shouldBe Some(IgaOperation(InterimVertex(4L), InterimVertex(2L), MergeAndEliminateInterim))
  }

  it("InterimVertex(2) -> RootVertex = MergeAndEliminateRoot") {
    IgaOperation.operationFor(InterimVertex(2L), RootVertex()) shouldBe Some(IgaOperation(InterimVertex(2L), RootVertex(), MergeAndEliminateRoot))
  }

  it("RootVertex -> InterimVertex(2) = BackwardsSubstituteRoot") {
    IgaOperation.operationFor(RootVertex(), InterimVertex(2L)) shouldBe Some(IgaOperation(RootVertex(), InterimVertex(2L), BackwardsSubstituteRoot))
  }

  it("InterimVertex(2) -> InterimVertex(4) = BackwardsSubstituteInterim") {
    IgaOperation.operationFor(InterimVertex(2L), InterimVertex(4L)) shouldBe Some(IgaOperation(InterimVertex(2L), InterimVertex(4L), BackwardsSubstituteInterim))
  }

  it("InterimVertex(4) -> BranchVertex(8) = BackwardsSubstituteBranch") {
    IgaOperation.operationFor(InterimVertex(4L), BranchVertex(8L)) shouldBe Some(IgaOperation(InterimVertex(4L), BranchVertex(8L), BackwardsSubstituteBranch))
  }

}
