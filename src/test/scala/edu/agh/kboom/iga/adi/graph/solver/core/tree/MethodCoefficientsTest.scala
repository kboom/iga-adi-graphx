package edu.agh.kboom.iga.adi.graph.solver.core.tree

import edu.agh.kboom.MatrixUtils
import org.scalatest.{FunSpec, Matchers}

class MethodCoefficientsTest extends FunSpec with Matchers {

  it("should initialise with proper coefficients") {
    val mA = MatrixUtils.emptyMatrixA

    MethodCoefficients.bind(mA)

    MatrixUtils.precisionCut(mA) shouldBe MatrixUtils.fromVector(6, 6)(
      0.05, 0.108, 0.008, 0.0, 0.0, 0.0,
      0.108, 0.45, 0.108, 0.0, 0.0, 0.0,
      0.008, 0.108, 0.05, 0.0, 0.0, 0.0,
      0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 0.0, 0.0, 0.0, 0.0, 0.0
    )
  }

}
