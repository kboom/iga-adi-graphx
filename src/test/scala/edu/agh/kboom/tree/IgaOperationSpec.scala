package edu.agh.kboom.tree

import edu.agh.kboom.IgaOperation
import edu.agh.kboom.production.MergeAndEliminateLeaf
import org.scalatest.FunSpec

class IgaOperationSpec extends FunSpec {

  it("LeafVertex -> BranchingVertex = MergeAndEliminateLeaves") {
    assert(IgaOperation.operationFor(LeafVertex(8), BranchVertex(4)) == IgaOperation(MergeAndEliminateLeaf()))
  }

  it("BranchVertex -> InterimVertex = MergeAndEliminateBranch") {
    assert(IgaOperation.operationFor(BranchVertex(8), InterimVertex(4)) == IgaOperation(MergeAndEliminateLeaf()))
  }

}
