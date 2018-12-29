package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.MatrixUtils.{generatedMatrixA, _}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{IgaElement, RootVertex}
import edu.agh.kboom.iga.adi.graph.solver.core.{MatrixA, MatrixB, Mesh}
import edu.agh.kboom.{ElementUtils, MethodSpec}


class packageTest extends MethodSpec {

  val AnyVertex = RootVertex()
  implicit val mesh: Mesh = Mesh(12, 12, 12, 12)

  describe("partialForwardElimination") {

    it("can eliminate unitary matrix") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        generatedMatrixA(Seq(fill(1)))
      )

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

    it("can eliminate first column") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        matrixA(
          1, 1, 1, 1, 1, 1,
          1, 2, 1, 1, 1, 1,
          1, 3, 1, 1, 1, 1,
          1, 4, 1, 1, 1, 1,
          1, 5, 1, 1, 1, 1,
          1, 6, 1, 1, 1, 1
        )
      )

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

    it("does not modify identity matrix") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        generatedMatrixA(Seq(identity))
      )

      partialForwardElimination(1, 6, mesh.yDofs)

      boundElement.mA shouldEqual MatrixA(identityMatrix(6))
    }

    it("eliminate 1 in 6x6 MatrixA") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        matrixA(
          4, 0, 1, 0, 1, 0,
          8, 1, 0, 1, 0, 1,
          4, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0,
          0, 3, 0, 0, 0, 0,
          1, 0, 0, 0, 0, 0
        )
      )

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

    it("should modify B if non-unitary A was used") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        generatedMatrixA(Seq(fill(1))),
        matrixB(
          4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16,
          0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0,
          0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        )
      )

      partialForwardElimination(1, 6, mesh.yDofs)

      boundElement.mB shouldEqual MatrixB(fromVector(6, 14)(
        +04.00, +08.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +16.00,
        -04.00, -07.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, -16.00,
        -04.00, -08.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, -16.00,
        -04.00, -08.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +24.00, +00.00, +00.00, +00.00, -16.00,
        -04.00, +04.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, -16.00,
        +00.00, -08.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, -16.00
      ))
    }

    it("eliminate not modify 6x14 B if unitary A is used") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        generatedMatrixA(Seq(identity)),
        matrixB(
          4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 16,
          0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0,
          0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        )
      )

      partialForwardElimination(1, 6, mesh.yDofs)

      boundElement.mB shouldEqual MatrixB(fromVector(6, 14)(
        4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 16,
        0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0,
        0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
      ))
    }

  }

  describe("swapDofs") {

    it("can swap 0 and 2 for 6") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        generatedMatrixA(Seq(index)),
        generatedMatrixB(Seq(index)),
        generatedMatrixX(Seq(index))
      )

      swapDofs(0, 2, 6, mesh.xDofs)

      boundElement should have(
        'mA (MatrixA(fromVector(6, 6)(
          +02.02, +02.01, +02.00, +02.03, +02.04, +02.05,
          +01.02, +01.01, +01.00, +01.03, +01.04, +01.05,
          +00.02, +00.01, +00.00, +00.03, +00.04, +00.05,
          +03.02, +03.01, +03.00, +03.03, +03.04, +03.05,
          +04.02, +04.01, +04.00, +04.03, +04.04, +04.05,
          +05.02, +05.01, +05.00, +05.03, +05.04, +05.05
        ))),
        'mB (MatrixB(fromVector(6, 14)(
          +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
          +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
          +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
          +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
          +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
          +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13
        ))),
        'mX (MatrixB(fromVector(6, 14)(
          +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
          +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
          +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
          +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
          +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
          +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13
        )))
      )
    }

    it("can swap 0 and 1 for 5") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        generatedMatrixA(Seq(index)),
        generatedMatrixB(Seq(index)),
        generatedMatrixX(Seq(index))
      )

      swapDofs(0, 1, 5, mesh.xDofs)

      boundElement should have(
        'mA (MatrixA(fromVector(6, 6)(
          +01.01, +01.00, +01.02, +01.03, +01.04, +00.05,
          +00.01, +00.00, +00.02, +00.03, +00.04, +01.05,
          +02.01, +02.00, +02.02, +02.03, +02.04, +02.05,
          +03.01, +03.00, +03.02, +03.03, +03.04, +03.05,
          +04.01, +04.00, +04.02, +04.03, +04.04, +04.05,
          +05.00, +05.01, +05.02, +05.03, +05.04, +05.05
        ))),
        'mB (MatrixB(fromVector(6, 14)(
          +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
          +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
          +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
          +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
          +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
          +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13
        ))),
        'mX (MatrixB(fromVector(6, 14)(
          +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
          +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
          +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
          +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
          +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
          +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13
        )))
      )
    }

  }

  describe("partialBackwardsSubstitution") {

    it("does not modify matrix A") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        generatedMatrixA(Seq(index))
      )

      partialBackwardsSubstitution(6, 6, mesh.yDofs)

      boundElement.mA shouldEqual generatedMatrixA(Seq(index))
    }

    it("does not modify matrix B") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        mB = generatedMatrixB(Seq(index))
      )

      partialBackwardsSubstitution(6, 6, mesh.yDofs)

      boundElement.mB shouldEqual generatedMatrixB(Seq(index))
    }

    it("can backwards substitute") {
      implicit val boundElement: IgaElement = ElementUtils.elementBoundTo(mesh, AnyVertex)(
        matrixA(
          1, 1, 1, 1, 1, 1,
          0, 1, 1, 1, 1, 1,
          0, 0, 1, 1, 1, 1,
          0, 0, 0, 1, 1, 1,
          0, 0, 0, 0, 1, 1,
          0, 0, 0, 0, 0, 1
        ),
        generatedMatrixB(Seq(fill(7), entry(1, 1)(10), entry(3, 4)(17))),
        generatedMatrixX(Seq(fill(0)))
      )

      partialBackwardsSubstitution(6, 6, mesh.yDofs)

      boundElement should have(
        'mX (matrixX(
          +00.00, -10.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +10.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, -17.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +17.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +07.00, +07.00, +07.00, +07.00, +07.00, +07.00, +07.00, +07.00, +07.00, +07.00, +07.00, +07.00, +07.00, +07.00
        ))
      )
    }

  }

}
