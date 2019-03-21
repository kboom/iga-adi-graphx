package edu.agh.kboom.iga.adi.graph.solver.core.initialisation

import edu.agh.kboom.iga.adi.graph.solver.core.tree.LeafVertex
import edu.agh.kboom.{DummyProblem, MethodSpec}
import org.apache.spark.mllib.linalg.Vectors

class VerticalInitializerTest extends MethodSpec with DummyProblem {

  /**
    * 0,1,2 --  8
    * 1,2,3 --  9
    * 2,3,4 -- 10
    * 3,4,5 -- 11
    */
  describe("verticesDependentOnRow") {

    it("is [8] for 0") {
      VerticalInitializer.verticesDependentOnRow(0) should contain only LeafVertex(8L)
    }

    it("is [8,9] for 1") {
      VerticalInitializer.verticesDependentOnRow(1) should contain theSameElementsAs Seq(8L, 9L).map(LeafVertex)
    }

    it("is [8,9,10] for 2") {
      VerticalInitializer.verticesDependentOnRow(2) should contain theSameElementsAs Seq(8L, 9L, 10L).map(LeafVertex)
    }

    it("is [9,10,11] for 3") {
      VerticalInitializer.verticesDependentOnRow(3) should contain theSameElementsAs Seq(9L, 10L, 11L).map(LeafVertex)
    }

    it("is [19] for 13") {
      VerticalInitializer.verticesDependentOnRow(13) should contain only LeafVertex(19L)
    }

    it("is [18,19] for 12") {
      VerticalInitializer.verticesDependentOnRow(12) should contain theSameElementsAs Seq(18L, 19L).map(LeafVertex)
    }

    it("is [17,18,19] for 11") {
      VerticalInitializer.verticesDependentOnRow(11) should contain theSameElementsAs Seq(17L, 18L, 19L).map(LeafVertex)
    }

  }

  describe("findLocalRow") {

    it("is 0 for v=8 and r=0") {
      VerticalInitializer.findLocalRowFor(LeafVertex(8L), 0) shouldBe 0
    }

    it("is 1 for v=8 and r=1") {
      VerticalInitializer.findLocalRowFor(LeafVertex(8L), 1) shouldBe 1
    }

    it("is 2 for v=8 and r=2") {
      VerticalInitializer.findLocalRowFor(LeafVertex(8L), 2) shouldBe 2
    }

    it("is 0 for v=9 and r=1") {
      VerticalInitializer.findLocalRowFor(LeafVertex(9L), 1) shouldBe 0
    }

    it("is 1 for v=9 and r=2") {
      VerticalInitializer.findLocalRowFor(LeafVertex(9L), 2) shouldBe 1
    }

    it("is 2 for v=9 and r=3") {
      VerticalInitializer.findLocalRowFor(LeafVertex(9L), 3) shouldBe 2
    }

    it("is 1 for v=19 and r=12") {
      VerticalInitializer.findLocalRowFor(LeafVertex(19L), 12) shouldBe 1
    }

    it("is 2 for v=19 and r=13") {
      VerticalInitializer.findLocalRowFor(LeafVertex(19L), 13) shouldBe 2
    }

  }

  describe("collocate") {

    val dummyVector = Vectors.dense(1, 2, 3)

    it("one") {
//      VerticalInitializer.collocate(IndexedRow(1, dummyVector)).keys should contain allElementsOf (Seq(8, 9))
    }

    it("two") {
//      VerticalInitializer.collocate(IndexedRow(0, dummyVector)).get(8).get.keys should contain allElementsOf (Seq(0, 1, 2))
    }

  }

}
