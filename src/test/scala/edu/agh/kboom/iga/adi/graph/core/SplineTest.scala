package edu.agh.kboom.iga.adi.graph.core

import edu.agh.kboom.MethodSpec

class SplineTest extends MethodSpec {

  describe("Spline1") {

    val spline1 = Spline1()

    it("is zero if lower than domain") {
      spline1.getValue(-0.1) shouldEqual 0
    }

    it("is zero if higher than domain") {
      spline1.getValue(1.1) shouldEqual 0
    }

    it("f(0) = 0") {
      spline1.getValue(0) shouldEqual 0
    }

    it("f(1) = 0.5") {
      spline1.getValue(1) shouldEqual 0.5
    }

    it("f(0.5) = 0.125") {
      spline1.getValue(0.5) shouldEqual 0.125
    }

    it("f'(0) = 0") {
      spline1.firstDerivativeValueAt(0) shouldEqual 0
    }

    it("f'(1) = 1") {
      spline1.firstDerivativeValueAt(1) shouldEqual 1
    }

    it("f'(0.5) = 0.5") {
      spline1.firstDerivativeValueAt(0.5) shouldEqual 0.5
    }

    it("f''(0) = 1") {
      spline1.secondDerivativeValueAt(0) shouldEqual 1
    }

    it("f''(1) = 1") {
      spline1.secondDerivativeValueAt(1) shouldEqual 1
    }

    it("f''(0.5) = 1") {
      spline1.secondDerivativeValueAt(0.5) shouldEqual 1
    }

  }

  describe("Spline2") {

    val spline2 = Spline2()

    it("is zero if lower than domain") {
      spline2.getValue(-0.1) shouldEqual 0
    }

    it("is zero if higher than domain") {
      spline2.getValue(1.1) shouldEqual 0
    }

    it("f(0) = 0.5") {
      spline2.getValue(0) shouldEqual 0.5
    }

    it("f(1) = 0.5") {
      spline2.getValue(1) shouldEqual 0.5
    }

    it("f(0.5) = 0.75") {
      spline2.getValue(0.5) shouldEqual 0.75
    }

    it("f'(0) = 1") {
      spline2.firstDerivativeValueAt(0) shouldEqual 1
    }

    it("f'(1) = -1") {
      spline2.firstDerivativeValueAt(1) shouldEqual -1
    }

    it("f'(0.5) = 0") {
      spline2.firstDerivativeValueAt(0.5) shouldEqual 0
    }

    it("f''(0) = -2") {
      spline2.secondDerivativeValueAt(0) shouldEqual -2
    }

    it("f''(1) = -2") {
      spline2.secondDerivativeValueAt(1) shouldEqual -2
    }

    it("f''(0.5) = -2") {
      spline2.secondDerivativeValueAt(0.5) shouldEqual -2
    }

  }

  describe("Spline3") {

    val spline3 = Spline3()

    it("is zero if lower than domain") {
      spline3.getValue(-0.1) shouldEqual 0
    }

    it("is zero if higher than domain") {
      spline3.getValue(1.1) shouldEqual 0
    }

    it("f(0) = 0.5") {
      spline3.getValue(0) shouldEqual 0.5
    }

    it("f(1) = 0") {
      spline3.getValue(1) shouldEqual 0
    }

    it("f(0.5) = 0.125") {
      spline3.getValue(0.5) shouldEqual 0.125
    }

    it("f'(0) = -1") {
      spline3.firstDerivativeValueAt(0) shouldEqual -1
    }

    it("f'(1) = 0") {
      spline3.firstDerivativeValueAt(1) shouldEqual 0
    }

    it("f'(0.5) = -0.5") {
      spline3.firstDerivativeValueAt(0.5) shouldEqual -0.5
    }

    it("f''(0) = 1") {
      spline3.secondDerivativeValueAt(0) shouldEqual 1
    }

    it("f''(1) = 1") {
      spline3.secondDerivativeValueAt(1) shouldEqual 1
    }

    it("f''(0.5) = 1") {
      spline3.secondDerivativeValueAt(0.5) shouldEqual 1
    }

  }

}
