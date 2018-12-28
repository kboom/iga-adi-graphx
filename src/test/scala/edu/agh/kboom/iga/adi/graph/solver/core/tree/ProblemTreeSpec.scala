package edu.agh.kboom.iga.adi.graph.solver.core.tree

import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree._
import org.scalatest._

class ProblemTreeSpec extends FunSpec {

  describe("Vertices in size 12 tree") {
    implicit val problemTree: ProblemTree = ProblemTree(12)

    it("has total height of 4") {
      assert(leafHeight == 4)
    }

    it("has branching height of 3") {
      assert(branchingHeight == 3)
    }

    it("has first index of branching row equal to 4") {
      assert(firstIndexOfBranchingRow == 4)
    }

    it("has last index of branching row equal to 7") {
      assert(lastIndexOfBranchingRow == 7)
    }

    it("has first index of leaf row equal to 8") {
      assert(firstIndexOfLeafRow == 8)
    }

    it("has last index of leaf row equal to 19") {
      assert(lastIndexOfLeafRow == 19)
    }

    it("has first index of 1 row equal to 1") {
      assert(firstIndexOfRow(1) == 1)
    }

    it("has first index of 2 row equal to 2") {
      assert(firstIndexOfRow(2) == 2)
    }

    it("has first index of 3 row equal to 4") {
      assert(firstIndexOfRow(3) == 4)
    }

    it("has first index of 4 row equal to 8") {
      assert(firstIndexOfRow(4) == 8)
    }

    it("has strength of 1st row equal to 1") {
      assert(strengthOfRow(1) == 1)
    }

    it("has strength of 2nd row equal to 2") {
      assert(strengthOfRow(2) == 2)
    }

    it("has strength of 3rd row equal to 4") {
      assert(strengthOfRow(3) == 4)
    }

    it("has strength of 4th row equal to 12") {
      assert(strengthOfRow(4) == 12)
    }

  }

  describe("Vertices in size 24 tree") {
    implicit val problemTree: ProblemTree = ProblemTree(24)

    it("has total height of 5") {
      assert(leafHeight == 5)
    }

    it("has branching height of 4") {
      assert(branchingHeight == 4)
    }

    it("has first index of branching row equal to 8") {
      assert(firstIndexOfBranchingRow == 8)
    }

    it("has last index of branching row equal to 15") {
      assert(lastIndexOfBranchingRow == 15)
    }

    it("has first index of leaf row equal to 16") {
      assert(firstIndexOfLeafRow == 16)
    }

    it("has last index of leaf row equal to 39") {
      assert(lastIndexOfLeafRow == 39)
    }

  }

}
