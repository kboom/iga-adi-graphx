package edu.agh.kboom

import edu.agh.kboom.Vertex._
import org.scalatest.FunSpec

class VertexSpec extends FunSpec {

  describe("Vertices in size 12 tree") {
    implicit val problemTree: ProblemTree = ProblemTree(12)

    it("vertex 1 should be interim") {
      assert(vertexOf(1) == InterimVertex(1))
    }

    it("vertex 2 should be interim") {
      assert(vertexOf(2) == InterimVertex(2))
    }

    it("vertex 4 should be branching") {
      assert(vertexOf(4) == BranchVertex(4))
    }

    it("vertex 7 should be branching") {
      assert(vertexOf(7) == BranchVertex(7))
    }

    it("vertex 8 should be leaf") {
      assert(vertexOf(8) == LeafVertex(8))
    }

    it("vertex 19 should be leaf") {
      assert(vertexOf(19) == LeafVertex(19))
    }

    it("row index of 1 should be 1") {
      assert(rowIndexOf(InterimVertex(1)) == 1)
    }

    it("row index of 2 should be 2") {
      assert(rowIndexOf(InterimVertex(2)) == 2)
    }

    it("row index of 3 should be 2") {
      assert(rowIndexOf(InterimVertex(3)) == 2)
    }

    it("row index of 4 should be 3") {
      assert(rowIndexOf(BranchVertex(4)) == 3)
    }

    it("row index of 7 should be 3") {
      assert(rowIndexOf(BranchVertex(7)) == 3)
    }

    it("row index of 8 should be 4") {
      assert(rowIndexOf(LeafVertex(8)) == 4)
    }

    it("row index of 15 should be 4") {
      assert(rowIndexOf(LeafVertex(15)) == 4)
    }

    it("vertex 1 should not be on top of branching level") {
      assert(!onTopOfBranchingRow(InterimVertex(1)))
    }

    it("vertex 2 should be on top of branching level") {
      assert(onTopOfBranchingRow(InterimVertex(2)))
    }

    it("left child of I2 should be B4") {
      assert(leftChildOf(InterimVertex(2)).contains(BranchVertex(4)))
    }

    it("left child of I3 should be B6") {
      assert(leftChildOf(InterimVertex(3)).contains(BranchVertex(6)))
    }

    it("left child of B4 should be L8") {
      assert(leftChildOf(BranchVertex(4)).contains(LeafVertex(8)))
    }

    it("left child of B7 should be L17") {
      assert(leftChildOf(BranchVertex(7)).contains(LeafVertex(17)))
    }

    it("children of I2 should be B4 and B5") {
      assert(childIndicesOf(InterimVertex(2)) == Seq(BranchVertex(4), BranchVertex(5)))
    }

    it("children of I3 should be B6 and B7") {
      assert(childIndicesOf(InterimVertex(3)) == Seq(BranchVertex(6), BranchVertex(7)))
    }

    it("children of B4 should be L8, L9, L10") {
      assert(childIndicesOf(BranchVertex(4)) == Seq(LeafVertex(8), LeafVertex(9), LeafVertex(10)))
    }

    it("children of B5 should be L11, L12, L13") {
      assert(childIndicesOf(BranchVertex(5)) == Seq(LeafVertex(11), LeafVertex(12), LeafVertex(13)))
    }

    it("I1 should have strength of 1") {
      assert(strengthOf(InterimVertex(1)) == 1)
    }

    it("I2 should have strength of 2") {
      assert(strengthOf(InterimVertex(2)) == 2)
    }

    it("I3 should have strength of 2") {
      assert(strengthOf(InterimVertex(3)) == 2)
    }

    it("B4 should have strength of 4") {
      assert(strengthOf(BranchVertex(4)) == 4)
    }

    it("B7 should have strength of 4") {
      assert(strengthOf(BranchVertex(7)) == 4)
    }

    it("L8 should have strength of 12") {
      assert(strengthOf(LeafVertex(8)) == 12)
    }

    it("offset left of 1 should be 1") {
      assert(offsetLeft(InterimVertex(1)) == 0)
    }

    it("offset left of 2 should be 0") {
      assert(offsetLeft(InterimVertex(2)) == 0)
    }

    it("offset left of 3 should be 1") {
      assert(offsetLeft(InterimVertex(3)) == 1)
    }

    it("offset left of 7 should be 3") {
      assert(offsetLeft(BranchVertex(7)) == 3)
    }

    it("offset left of 15 should be 7") {
      assert(offsetLeft(LeafVertex(15)) == 7)
    }

    it("segment of 1 should be (0, 12)") {
      assert(segmentOf(InterimVertex(1)) == (0, 12))
    }

    it("segment of 2 should be (0, 6)") {
      assert(segmentOf(InterimVertex(2)) == (0, 6))
    }

    it("segment of 3 should be (6, 12)") {
      assert(segmentOf(InterimVertex(3)) == (6, 12))
    }

    it("segment of 4 should be (0, 3)") {
      assert(segmentOf(BranchVertex(4)) == (0, 3))
    }

    it("segment of 5 should be (3, 6)") {
      assert(segmentOf(BranchVertex(5)) == (3, 6))
    }

    it("segment of 6 should be (6, 9)") {
      assert(segmentOf(BranchVertex(6)) == (6, 9))
    }

    it("segment of 7 should be (9, 12)") {
      assert(segmentOf(BranchVertex(7)) == (9, 12))
    }

    it("segment of 8 should be (0, 1)") {
      assert(segmentOf(LeafVertex(8)) == (0, 1))
    }

    it("segment of 9 should be (1, 2)") {
      assert(segmentOf(LeafVertex(9)) == (1, 2))
    }

    it("segment of 19 should be (11, 12)") {
      assert(segmentOf(LeafVertex(19)) == (11, 12))
    }
  }


}
