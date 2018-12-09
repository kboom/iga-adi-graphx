package edu.agh.kboom

import edu.agh.kboom.core.production._
import edu.agh.kboom.core.tree._
import edu.agh.kboom.core.{IgaOperation, IgaTasks}
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

        IgaOperation(InterimVertex(2),RootVertex(),SolveRoot),
        IgaOperation(InterimVertex(3),RootVertex(),SolveRoot),

        IgaOperation(RootVertex(),InterimVertex(2),BackwardsSubstituteInterim),
        IgaOperation(RootVertex(),InterimVertex(3),BackwardsSubstituteInterim),

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

    it("should contain all operations") {
      IgaTasks.generateOperations(problemTree) should contain theSameElementsAs Seq(
        IgaOperation(InterimVertex(2),RootVertex(),SolveRoot),
        IgaOperation(RootVertex(),InterimVertex(2),BackwardsSubstituteInterim),
        IgaOperation(InterimVertex(3),RootVertex(),SolveRoot),
        IgaOperation(RootVertex(),InterimVertex(3),BackwardsSubstituteInterim),
        IgaOperation(InterimVertex(4),InterimVertex(2),MergeAndEliminateInterim),
        IgaOperation(InterimVertex(2),InterimVertex(4),BackwardsSubstituteInterim),
        IgaOperation(InterimVertex(5),InterimVertex(2),MergeAndEliminateInterim),
        IgaOperation(InterimVertex(2),InterimVertex(5),BackwardsSubstituteInterim),
        IgaOperation(InterimVertex(6),InterimVertex(3),MergeAndEliminateInterim),
        IgaOperation(InterimVertex(3),InterimVertex(6),BackwardsSubstituteInterim),
        IgaOperation(InterimVertex(7),InterimVertex(3),MergeAndEliminateInterim),
        IgaOperation(InterimVertex(3),InterimVertex(7),BackwardsSubstituteInterim),
        IgaOperation(BranchVertex(8),InterimVertex(4),MergeAndEliminateBranch),
        IgaOperation(InterimVertex(4),BranchVertex(8),BackwardsSubstituteBranch),
        IgaOperation(BranchVertex(9),InterimVertex(4),MergeAndEliminateBranch),
        IgaOperation(InterimVertex(4),BranchVertex(9),BackwardsSubstituteBranch),
        IgaOperation(BranchVertex(10),InterimVertex(5),MergeAndEliminateBranch),
        IgaOperation(InterimVertex(5),BranchVertex(10),BackwardsSubstituteBranch),
        IgaOperation(BranchVertex(11),InterimVertex(5),MergeAndEliminateBranch),
        IgaOperation(InterimVertex(5),BranchVertex(11),BackwardsSubstituteBranch),
        IgaOperation(BranchVertex(12),InterimVertex(6),MergeAndEliminateBranch),
        IgaOperation(InterimVertex(6),BranchVertex(12),BackwardsSubstituteBranch),
        IgaOperation(BranchVertex(13),InterimVertex(6),MergeAndEliminateBranch),
        IgaOperation(InterimVertex(6),BranchVertex(13),BackwardsSubstituteBranch),
        IgaOperation(BranchVertex(14),InterimVertex(7),MergeAndEliminateBranch),
        IgaOperation(InterimVertex(7),BranchVertex(14),BackwardsSubstituteBranch),
        IgaOperation(BranchVertex(15),InterimVertex(7),MergeAndEliminateBranch),
        IgaOperation(InterimVertex(7),BranchVertex(15),BackwardsSubstituteBranch),
        IgaOperation(LeafVertex(16),BranchVertex(8),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(17),BranchVertex(8),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(18),BranchVertex(8),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(19),BranchVertex(9),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(20),BranchVertex(9),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(21),BranchVertex(9),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(22),BranchVertex(10),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(23),BranchVertex(10),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(24),BranchVertex(10),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(25),BranchVertex(11),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(26),BranchVertex(11),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(27),BranchVertex(11),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(28),BranchVertex(12),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(29),BranchVertex(12),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(30),BranchVertex(12),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(31),BranchVertex(13),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(32),BranchVertex(13),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(33),BranchVertex(13),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(34),BranchVertex(14),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(35),BranchVertex(14),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(36),BranchVertex(14),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(37),BranchVertex(15),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(38),BranchVertex(15),MergeAndEliminateLeaf),
        IgaOperation(LeafVertex(39),BranchVertex(15),MergeAndEliminateLeaf)
      )
    }

    it("should contain Leaf(16)-[Merge And Eliminate Leaf]-Branch(8)") {
      IgaTasks.generateOperations(problemTree) should contain (
        IgaOperation(LeafVertex(16), BranchVertex(8), MergeAndEliminateLeaf)
      )
    }
  }

}
