package edu.agh.kboom.core.production

import edu.agh.kboom.ElementUtils.elementBoundTo
import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom.core._
import edu.agh.kboom.core.tree.{InterimVertex, RootVertex}
import edu.agh.kboom.{DummyProblem, MatrixColors, SubjectSpec}

class SolveRootTest extends SubjectSpec
  with DummyProblem
  with MatrixColors {

  val Parent = RootVertex()
  val LeftChild = InterimVertex(2)
  val RightChild = InterimVertex(3)

  "emit" when {

    val dstElement = elementBoundTo(TestMesh, Parent)()

    "applied on left child" should {

      val srcElement = elementBoundTo(TestMesh, LeftChild)(
        generatedMatrixA(Seq(index)),
        generatedMatrixB(Seq(index))
      )

      "is not empty" in {
        SolveRoot.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        SolveRoot.emit(srcElement, dstElement).get should have(
          'ca (MatrixA(fromVector(6, 6)(
            +02.02, +02.03, +02.04, +02.05, +00.00, +00.00,
            +03.02, +03.03, +03.04, +03.05, +00.00, +00.00,
            +04.02, +04.03, +04.04, +04.05, +00.00, +00.00,
            +05.02, +05.03, +05.04, +05.05, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )))
        )
      }

      "emits matrix B translated by one row" in {
        SolveRoot.emit(srcElement, dstElement).get should have(
          'cb (MatrixB(fromVector(6, 14)(
            +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
            +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
            +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
            +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )))
        )
      }

    }

    "applied on right child" should {

      val srcElement = elementBoundTo(TestMesh, RightChild)(
        generatedMatrixA(Seq(index))
      )

      "is not empty" in {
        SolveRoot.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        SolveRoot.emit(srcElement, dstElement).get should have(
          'ca (MatrixA(fromVector(6, 6)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +02.02, +02.03, +02.04, +02.05,
            +00.00, +00.00, +03.02, +03.03, +03.04, +03.05,
            +00.00, +00.00, +04.02, +04.03, +04.04, +04.05,
            +00.00, +00.00, +05.02, +05.03, +05.04, +05.05
          )))
        )
      }

      "emits matrix B translated by one row" in {
        SolveRoot.emit(srcElement, dstElement).get should have(
          'cb (MatrixB(fromVector(6, 14)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
            +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
            +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
            +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13
          )))
        )
      }

    }

  }

  "merge" when {

    val redMsg = SolveRootMessage(
      generatedMatrixA(Seq(WhiteFabric, RedFeature)),
      generatedMatrixB(Seq(GreyFabric, RedFeature))
    )
    val greenMsg = SolveRootMessage(
      generatedMatrixA(Seq(GreyFabric, BlueFeature)),
      generatedMatrixB(Seq(WhiteFabric, BlueFeature))
    )

    "two messages" should {

      "produce a sum of matrices" in {
        SolveRoot.merge(redMsg, greenMsg) shouldBe SolveRootMessage(
          generatedMatrixA(Seq(WhiteFabric, GreyFabric, RedFeature, BlueFeature)),
          generatedMatrixB(Seq(WhiteFabric, GreyFabric, RedFeature, BlueFeature))
        )
      }

    }

  }

  "consume" when {

    val msg = SolveRootMessage(
      generatedMatrixA(Seq(fill(0), entry(2, 1)(3))),
      generatedMatrixB(Seq(fill(0), entry(2, 1)(5)))
    )

    "something" in {
      val dstElement = elementBoundTo(TestMesh, Parent)(
        matrixA(
          1, 1, 1, 1, 1, 1,
          1, 2, 1, 1, 2, 1,
          1, 1, 3, 1, 1, 1,
          1, 1, 1, 4, 1, 1,
          1, 1, 1, 1, 5, 1,
          1, 1, 1, 1, 1, 6
        ),
        generatedMatrixB(Seq(entry(0, 0)(3), entry(1, 6)(4))),
        generatedMatrixX(Seq(fill(1), entry(0, 0)(2),  entry(4, 10)(2)))
      )

      SolveRoot.consume(dstElement, msg)

      dstElement should have(
        'mA (matrixA(
          +01.00, +01.00, +01.00, +01.00, +01.00, +01.00,
          +00.00, +01.00, +00.00, +00.00, +01.00, +00.00,
          +00.00, +00.00, +01.00, +00.00, -01.50, +00.00,
          +00.00, +00.00, +00.00, +01.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +01.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +01.00
        )),
        'mB (matrixB(
          +03.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          -03.00, +00.00, +00.00, +00.00, +00.00, +00.00, +04.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +03.00, +02.50, +00.00, +00.00, +00.00, +00.00, -06.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          -01.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          -00.75, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          -00.60, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
        )),
        'mX (matrixX(
          +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
          +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
          +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
          +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
          +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
          +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13
        ))
      )
    }

  }

}
