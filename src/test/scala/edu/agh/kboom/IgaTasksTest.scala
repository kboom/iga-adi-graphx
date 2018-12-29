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

        IgaOperation(LeafVertex(8),BranchVertex(4),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(9),BranchVertex(4),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(10),BranchVertex(4),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(11),BranchVertex(5),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(12),BranchVertex(5),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(13),BranchVertex(5),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(14),BranchVertex(6),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(15),BranchVertex(6),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(16),BranchVertex(6),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(17),BranchVertex(7),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(18),BranchVertex(7),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(19),BranchVertex(7),MergeAndEliminateLeaf),

        IgaOperation(BranchVertex(4),InterimVertex(2),MergeAndEliminateBranch),
        IgaOperation(BranchVertex(5),InterimVertex(2),MergeAndEliminateBranch),
        IgaOperation(BranchVertex(6),InterimVertex(3),MergeAndEliminateBranch),
        IgaOperation(BranchVertex(7),InterimVertex(3),MergeAndEliminateBranch),

        IgaOperation(InterimVertex(2),RootVertex(),MergeAndEliminateRoot),
        IgaOperation(InterimVertex(3),RootVertex(),MergeAndEliminateRoot),

        IgaOperation(RootVertex(),InterimVertex(2),BackwardsSubstituteRoot),
        IgaOperation(RootVertex(),InterimVertex(3),BackwardsSubstituteRoot),

        IgaOperation(InterimVertex(2),BranchVertex(4),BackwardsSubstituteBranch),
        IgaOperation(InterimVertex(2),BranchVertex(5),BackwardsSubstituteBranch),
        IgaOperation(InterimVertex(3),BranchVertex(6),BackwardsSubstituteBranch),
        IgaOperation(InterimVertex(3),BranchVertex(7),BackwardsSubstituteBranch)
      )
    }

    it("should contain Leaf(8)-[Merge And Eliminate Leaf]-Branch(4)") {
      IgaTasks.generateOperations(problemTree) should contain (
        IgaOperation(LeafVertex(8), BranchVertex(4), MergeAndEliminateLeaf)
      )
    }
  }

  describe("Problem 24") {
    val problemTree: ProblemTree = ProblemTree(24)

    it("should contain Leaf(16)-[Merge And Eliminate Leaf]-Branch(8)") {
      IgaTasks.generateOperations(problemTree) should contain (
        IgaOperation(LeafVertex(16), BranchVertex(8), MergeAndEliminateLeaf)
      )
    }
  }

}
