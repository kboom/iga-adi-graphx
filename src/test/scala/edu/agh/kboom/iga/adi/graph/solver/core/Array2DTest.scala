package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.MatrixUtils.fromVector
import edu.agh.kboom.TestMatrix
import edu.agh.kboom.iga.adi.graph.solver.core.Array2D.move
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

  it("Can replace an entry with a value") {
    TestMatrix(fromVector(2, 2)(
      1, 0,
      0, 1
    )).replace(1, 0, 9) shouldBe TestMatrix(fromVector(2, 2)(
      1, 0,
      9, 1
    ))
  }

  it("Can map an entry") {
    TestMatrix(fromVector(2, 2)(
      1, 0,
      3, 1
    )).mapEntry(1, 0)(_ * 3 + 1) shouldBe TestMatrix(fromVector(2, 2)(
      1, 0,
      10, 1
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
