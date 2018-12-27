package edu.agh.kboom.core.initialisation

import edu.agh.kboom.{DummyProblem, MethodSpec}

class VerticalInitializerTest extends MethodSpec with DummyProblem {

  /**
    * 0,1,2 --  8
    * 1,2,3 --  9
    * 2,3,4 -- 10
    * 3,4,5 -- 11
    */
  describe("verticesDependentOnRow") {

    it("is [8] for 0") {
      VerticalInitializer.verticesDependentOnRow(0) should contain only 8
    }

    it("is [8,9] for 1") {
      VerticalInitializer.verticesDependentOnRow(1) should contain theSameElementsAs Seq(8, 9)
    }

    it("is [8,9,10] for 2") {
      VerticalInitializer.verticesDependentOnRow(2) should contain theSameElementsAs Seq(8, 9, 10)
    }

    it("is [9,10,11] for 3") {
      VerticalInitializer.verticesDependentOnRow(3) should contain theSameElementsAs Seq(9, 10, 11)
    }

    it("is [19] for 13") {
      VerticalInitializer.verticesDependentOnRow(13) should contain only 19
    }

    it("is [18,19] for 12") {
      VerticalInitializer.verticesDependentOnRow(12) should contain theSameElementsAs Seq(18,19)
    }

    it("is [17,18,19] for 11") {
      VerticalInitializer.verticesDependentOnRow(11) should contain theSameElementsAs Seq(17,18,19)
    }

  }

}
