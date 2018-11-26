package edu.agh.kboom.core.production

import edu.agh.kboom.MatrixUtils.{identityMatrix, fromVector}
import edu.agh.kboom.UnitSpec
import edu.agh.kboom.core.tree.{BoundElement, Element, RootVertex}
import edu.agh.kboom.core.{MatrixA, MatrixB, Mesh}


class packageTest extends UnitSpec {

  trait IgaContext {
    implicit val mesh: Mesh = Mesh(12, 12, 12, 12)
    implicit val element: Element = Element.createForX(mesh)
    implicit val boundElement: BoundElement = BoundElement(RootVertex(), element)
  }

  it should "eliminate unitary matrix" in new IgaContext {
    element.mA.add(MatrixA(fromVector(6, 6)(
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1
    )))

    partialForwardElimination(1, 6, mesh.yDofs)

    boundElement.mA shouldEqual MatrixA(fromVector(6, 6)(
      1, 1, 1, 1, 1, 1,
      0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0
    ))
  }

  it should "eliminate first column" in new IgaContext {
    element.mA.add(MatrixA(fromVector(6, 6)(
      1, 1, 1, 1, 1, 1,
      1, 2, 1, 1, 1, 1,
      1, 3, 1, 1, 1, 1,
      1, 4, 1, 1, 1, 1,
      1, 5, 1, 1, 1, 1,
      1, 6, 1, 1, 1, 1
    )))

    partialForwardElimination(1, 6, mesh.yDofs)

    boundElement.mA shouldEqual MatrixA(fromVector(6, 6)(
      1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
      0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 2.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 3.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 4.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 5.0, 0.0, 0.0, 0.0, 0.0
    ))
  }

  it should "not modify identity matrix" in new IgaContext {
    element.mA.add(MatrixA(identityMatrix(6)))

    partialForwardElimination(1, 6, mesh.yDofs)

    boundElement.mA shouldEqual MatrixA(identityMatrix(6))
  }

  it should "eliminate 1 in 6x6 MatrixA" in new IgaContext {
    element.mA.add(MatrixA(fromVector(6, 6)(
      4, 0, 1, 0, 1, 0,
      8, 1, 0, 1, 0, 1,
      4, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0,
      0, 3, 0, 0, 0, 0,
      1, 0, 0, 0, 0, 0
    )))

    partialForwardElimination(1, 6, mesh.yDofs)

    boundElement.mA shouldEqual MatrixA(fromVector(6, 6)(
      1.0, 0.0, 0.25, 0.0, 0.25, 0.0,
      0.0, 1.0, -2.0, 1.0, -2.0, 1.0,
      0.0, 0.0, -1.0, 0.0, -1.0, 0.0,
      0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 3.0, 0.0, 0.0, 0.0, 0.0,
      0.0, 0.0, -0.25, 0.0, -0.25, 0.0
    ))
  }

  it should "should modify B if non-unitary A was used" in new IgaContext {
    element.mA.add(MatrixA(fromVector(6, 6)(
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1
    )))

    element.mB.add(MatrixB(fromVector(6, 15)(
      4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 16,
      0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0,
      0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    )))

    partialForwardElimination(1, 6, mesh.yDofs)

    boundElement.mB shouldEqual MatrixB(fromVector(6, 15)(
      4.0, 8.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 16.0, 16.0,
      -4.0, -7.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -16.0, 0.0,
      -4.0, -8.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -16.0, 0.0,
      -4.0, -8.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 24.0, 0.0, 0.0, 0.0, -16.0, 0.0,
      -4.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -16.0, 0.0,
      0.0, -8.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -16.0, 0.0
    ))
  }

  it should "eliminate not modify 6x15 B if unitary A is used" in new IgaContext {
    element.mA.add(MatrixA(identityMatrix(6)))

    element.mB.add(MatrixB(fromVector(6, 15)(
      4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 16,
      0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0,
      0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    )))

    partialForwardElimination(1, 6, mesh.yDofs)

    boundElement.mB shouldEqual MatrixB(fromVector(6, 15)(
      4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 16,
      0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0,
      0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    ))
  }

}
