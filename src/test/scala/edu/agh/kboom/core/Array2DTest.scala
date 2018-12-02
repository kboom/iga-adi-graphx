package edu.agh.kboom.core

import edu.agh.kboom.MatrixUtils.fromVector
import edu.agh.kboom.TestMatrix
import edu.agh.kboom.core.Array2D.move
import org.scalatest.{FunSpec, Matchers}


sealed class Array2DTest extends FunSpec with Matchers {

  it("Can add matrices") {
    TestMatrix(fromVector(2, 2)(
      1, 0,
      0, 1
    )) + TestMatrix(fromVector(2, 2)(
      0, 1,
      1, 0
    )) shouldBe TestMatrix(fromVector(2, 2)(
      1, 1,
      1, 1
    ))
  }

  it("Can move to destination using one element") {
    TestMatrix(fromVector(3, 3)(
      0, 0, 0,
      0, 1, 0,
      0, 0, 0
    )).transformedBy(1 to 1, 1 to 1)()(move(1, 1)) shouldBe TestMatrix(fromVector(3, 3)(
      0, 0, 0,
      0, 0, 0,
      0, 0, 1
    ))
  }

  it("Can move to destination") {
    TestMatrix(fromVector(3, 3)(
      0, 0, 0,
      0, 1, 0,
      0, 0, 0
    )).transformedBy(0 to 1, 0 to 1)()(move(1, 1)) shouldBe TestMatrix(fromVector(3, 3)(
      0, 0, 0,
      0, 0, 0,
      0, 0, 1
    ))
  }

  it("Can move from source") {
    TestMatrix(fromVector(3, 3)(
      0, 0, 0,
      0, 1, 0,
      0, 0, 0
    )).transformedBy(0 to 1, 0 to 1)(move(1, 1))() shouldBe TestMatrix(fromVector(3, 3)(
      1, 0, 0,
      0, 0, 0,
      0, 0, 0
    ))
  }

  it("Can move both ways") {
    TestMatrix(fromVector(3, 3)(
      0, 0, 0,
      0, 1, 0,
      0, 0, 0
    )).transformedBy(0 to 1, 0 to 1)(move(1, 1))(move(1, 1)) shouldBe TestMatrix(fromVector(3, 3)(
      0, 0, 0,
      0, 1, 0,
      0, 0, 0
    ))
  }

}