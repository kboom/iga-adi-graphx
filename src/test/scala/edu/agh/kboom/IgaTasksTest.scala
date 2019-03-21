package edu.agh.kboom

import edu.agh.kboom.iga.adi.graph.solver.core.production._
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaOperation, IgaTasks}
import org.scalatest.{FunSpec, Matchers}

class IgaTasksTest extends FunSpec with Matchers {

  describe("Problem 12") {
    val problemTree: ProblemTree = ProblemTree(12)

    it("should contain all operations") {
      IgaTasks.generateOperations(problemTree) should contain theSameElementsAs Seq(

        IgaOperation(LeafVertex(8L),BranchVertex(4L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(9L),BranchVertex(4L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(10L),BranchVertex(4L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(11L),BranchVertex(5L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(12L),BranchVertex(5L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(13L),BranchVertex(5L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(14L),BranchVertex(6L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(15L),BranchVertex(6L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(16L),BranchVertex(6L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(17L),BranchVertex(7L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(18L),BranchVertex(7L),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(19L),BranchVertex(7L),MergeAndEliminateLeaf),

        IgaOperation(BranchVertex(4L),InterimVertex(2L),MergeAndEliminateBranch),
        IgaOperation(BranchVertex(5L),InterimVertex(2L),MergeAndEliminateBranch),
        IgaOperation(BranchVertex(6L),InterimVertex(3L),MergeAndEliminateBranch),
        IgaOperation(BranchVertex(7L),InterimVertex(3L),MergeAndEliminateBranch),

        IgaOperation(InterimVertex(2L),RootVertex(),MergeAndEliminateRoot),
        IgaOperation(InterimVertex(3L),RootVertex(),MergeAndEliminateRoot),

        IgaOperation(RootVertex(),InterimVertex(2L),BackwardsSubstituteRoot),
        IgaOperation(RootVertex(),InterimVertex(3L),BackwardsSubstituteRoot),

        IgaOperation(InterimVertex(2L),BranchVertex(4L),BackwardsSubstituteBranch),
        IgaOperation(InterimVertex(2L),BranchVertex(5L),BackwardsSubstituteBranch),
        IgaOperation(InterimVertex(3L),BranchVertex(6L),BackwardsSubstituteBranch),
        IgaOperation(InterimVertex(3L),BranchVertex(7L),BackwardsSubstituteBranch)
      )
    }

    it("should contain Leaf(8)-[Merge And Eliminate Leaf]-Branch(4)") {
      IgaTasks.generateOperations(problemTree) should contain (
        IgaOperation(LeafVertex(8L), BranchVertex(4L), MergeAndEliminateLeaf)
      )
    }
  }

  describe("Problem 24") {
    val problemTree: ProblemTree = ProblemTree(24)

    it("should contain Leaf(16)-[Merge And Eliminate Leaf]-Branch(8)") {
      IgaTasks.generateOperations(problemTree) should contain (
        IgaOperation(LeafVertex(16L), BranchVertex(8L), MergeAndEliminateLeaf)
      )
    }
  }

}
