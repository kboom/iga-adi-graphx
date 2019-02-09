package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.MatrixUtils.{fill, index}
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixUtil.DenseMatrixUtil
import edu.agh.kboom.{MatrixUtils, MethodSpec}

class MatrixUtilTest extends MethodSpec {

  describe("swapRows") {

    it("swaps 0 and 1 row") {
      MatrixUtils.assembleMatrix(3, 4)(Seq(index)).swapRows(0, 1) shouldBe MatrixUtils.fromVector(3, 4)(
        1.0, 1.01, 1.02, 1.03,
        0, 0.01, 0.02, 0.03,
        2.0, 2.01, 2.02, 2.03
      )
    }

  }

  describe("swapCols") {

    it("swaps 0 and 2 col") {
      MatrixUtils.assembleMatrix(3, 4)(Seq(index)).swapCols(0, 2) shouldBe MatrixUtils.fromVector(3, 4)(
        0.02, 0.01, 0, 0.03,
        1.02, 1.01, 1.0, 1.03,
        2.02, 2.01, 2.0, 2.03
      )
    }

  }

//  describe("::+") {
//
//    val addition = MatrixUtils.assembleMatrix(4, 4)(Seq(fill(1)))
//
//    it("swaps 0 and 1 row") {
//      MatrixUtils.assembleMatrix(4, 4)(Seq(fill(1)))(1 to 2, 1 to 2) ::+ addition(0 to 1, 0 to 1) shouldBe MatrixUtils.fromVector(4, 4)(
//        1.0, 1.01, 1.02, 1.03,
//        0, 0.01, 0.02, 0.03,
//        2.0, 2.01, 2.02, 2.03,
//        2.0, 2.01, 2.02, 2.03
//      )
//    }
//
//  }

}
