package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.ElementUtils.elementBoundTo
import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom.iga.adi.graph.solver.core.tree.InterimVertex
import edu.agh.kboom.iga.adi.graph.solver.core.{MatrixA, MatrixB}
import edu.agh.kboom.{DummyProblem, MatrixColors, SubjectSpec}

sealed class MergeAndEliminateInterimTest extends SubjectSpec
  with DummyProblem
  with MatrixColors {

  val Parent = InterimVertex(2)
  val LeftChild = InterimVertex(4)
  val RightChild = InterimVertex(5)

  "emit" when {

    val dstElement = elementBoundTo(TestMesh, Parent)()

    "applied on left child" should {

      val srcElement = elementBoundTo(TestMesh, LeftChild)(
        generatedMatrixA(Seq(index)),
        generatedMatrixB(Seq(index))
      )

      "is not empty" in {
        MergeAndEliminateInterim.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        MergeAndEliminateInterim.emit(srcElement, dstElement).get should have(
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
        MergeAndEliminateInterim.emit(srcElement, dstElement).get should have(
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
        MergeAndEliminateInterim.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        MergeAndEliminateInterim.emit(srcElement, dstElement).get should have(
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

      "emits matrix B translated by two rows" in {
        MergeAndEliminateInterim.emit(srcElement, dstElement).get should have(
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

    val redMsg = MergeAndEliminateInterimMessage(
      generatedMatrixA(Seq(WhiteFabric, RedFeature)),
      generatedMatrixB(Seq(GreyFabric, RedFeature))
    )
    val greenMsg = MergeAndEliminateInterimMessage(
      generatedMatrixA(Seq(GreyFabric, BlueFeature)),
      generatedMatrixB(Seq(WhiteFabric, BlueFeature))
    )

    "two messages" should {

      "produce a sum of matrices" in {
        MergeAndEliminateInterim.merge(redMsg, greenMsg) shouldBe MergeAndEliminateInterimMessage(
          generatedMatrixA(Seq(WhiteFabric, GreyFabric, RedFeature, BlueFeature)),
          generatedMatrixB(Seq(WhiteFabric, GreyFabric, RedFeature, BlueFeature))
        )
      }

    }

  }

  "consume" when {

    val msg = MergeAndEliminateInterimMessage(
      generatedMatrixA(Seq(fill(0), entry(2, 1)(3))),
      generatedMatrixB(Seq(fill(0), entry(2, 1)(5)))
    )

    "something" in {
      val dstElement = elementBoundTo(TestMesh, Parent)(
        generatedMatrixA(Seq(fill(1), entry(2, 3)(-1), entry(0, 5)(-2))),
        generatedMatrixB(Seq(entry(0, 0)(1), entry(1, 1)(3), entry(3, 5)(7), entry(2, 8)(15))),
        generatedMatrixX(Seq(index))
      )

      MergeAndEliminateInterim.consume(dstElement, msg)

      dstElement should have(
        'mA (matrixA(
          +01.00, +00.00, +01.00, +04.00, +01.00, +01.00,
          +00.00, +01.00, +00.00, -03.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, -02.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
        )),
        'mB (matrixB(
          +00.00, +05.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +15.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, -05.00, +00.00, +00.00, +00.00, +07.00, +00.00, +00.00, -15.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +01.00, +00.00, +00.00, +00.00, +00.00, -07.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +03.00, +00.00, +00.00, +00.00, -07.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, -07.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, -07.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
        )),
        'mX (matrixX(
          +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
          +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
          +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
          +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
          +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
          +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13
        ))
      )
    }

  }
}

