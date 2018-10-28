package edu.agh.kboom.tree

import edu.agh.kboom.IgaFSM.initialState
import edu.agh.kboom.{IgaFSM, InitialState, InitializeState}
import edu.agh.kboom.tree.ProblemTree.{branchingHeight, leafHeight}
import org.scalatest.FunSpec

class IgaFsmSpec extends FunSpec {

  it("produces initial state") {
    assert(initialState() == InitialState(0))
  }

  describe("FSM on size 12 tree") {
    implicit val problemTree: ProblemTree = ProblemTree(12)

    it("next state after InitialState(0) is InitializeState(1)") {
      assert(IgaFSM.nextState(InitialState(0)) == InitializeState(1))
    }
  }

}
