package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.ElementUtils.elementBoundTo
import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{BranchVertex, LeafVertex}
import edu.agh.kboom.{DummyProblem, MatrixColors, MatrixUtils, SubjectSpec}

class MergeAndEliminateLeafTest extends SubjectSpec
  with DummyProblem
  with MatrixColors {

  val Parent = BranchVertex(4)
  val LeftChild = LeafVertex(8)
  val MiddleChild = LeafVertex(9)
  val RightChild = LeafVertex(10)

  "emit" when {

    val dstElement = elementBoundTo(TestMesh, Parent)()

    "applied on left child" should {

      val srcElement = elementBoundTo(TestMesh, LeftChild)(
        generatedMatrixA(Seq(index)),
        generatedMatrixB(Seq(index))
      )

      "is not empty" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits A" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).map(_.ca).map(MatrixUtils.exploded).get should equal(
          fromVector(6, 6)(
            0.0, 0.01, 0.02, 0.0, 0.0, 0.0,
            1.0, 1.01, 1.02, 0.0, 0.0, 0.0,
            2.0, 2.01, 2.02, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0
          )
        )
      }

      "emits B" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).map(_.cb).map(MatrixUtils.exploded).get should equal(
          fromVector(6, 14)(
            0.0, 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.11, 0.12, 0.13,
            1.0, 1.01, 1.02, 1.03, 1.04, 1.05, 1.06, 1.07, 1.08, 1.09, 1.1, 1.11, 1.12, 1.13,
            2.0, 2.01, 2.02, 2.03, 2.04, 2.05, 2.06, 2.07, 2.08, 2.09, 2.1, 2.11, 2.12, 2.13,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
          )
        )
      }

    }

    "applied on middle child" should {

      val srcElement = elementBoundTo(TestMesh, MiddleChild)(
        generatedMatrixA(Seq(index))
      )

      "is not empty" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).map(_.ca).map(MatrixUtils.exploded).get should equal(
          fromVector(6, 6)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.01, +00.02, +00.00, +00.00,
            +00.00, +01.00, +01.01, +01.02, +00.00, +00.00,
            +00.00, +02.00, +02.01, +02.02, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )
        )
      }

      "emits matrix B translated by one row" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).map(_.cb).map(MatrixUtils.exploded).get should equal(
          fromVector(6, 14)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
            +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
            +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )
        )
      }

    }

    "applied on right child" should {

      val srcElement = elementBoundTo(TestMesh, RightChild)(
        generatedMatrixA(Seq(index))
      )

      "is not empty" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).map(_.ca).map(MatrixUtils.exploded).get should equal(
          fromVector(6, 6)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.01, +00.02, +00.00,
            +00.00, +00.00, +01.00, +01.01, +01.02, +00.00,
            +00.00, +00.00, +02.00, +02.01, +02.02, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )
        )
      }

      "emits matrix B translated by one row" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).map(_.cb).map(MatrixUtils.exploded).get should be(
          fromVector(6, 14)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
            +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
            +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )
        )
      }

    }

  }

  "merge" when {

    val redMsg = MergeAndEliminateLeafMessage(
      generatedMatrixA(Seq(WhiteFabric, RedFeature)),
      generatedMatrixB(Seq(GreyFabric, RedFeature))
    )
    val greenMsg = MergeAndEliminateLeafMessage(
      generatedMatrixA(Seq(GreyFabric, BlueFeature, GreenFeature)),
      generatedMatrixB(Seq(WhiteFabric, BlueFeature, GreenFeature))
    )

    "two messages" should {

      "produce a sum of matrices" in {
        MergeAndEliminateLeaf.merge(redMsg, greenMsg) should have(
          'ca (generatedMatrixA(Seq(WhiteFabric, GreyFabric, RedFeature, BlueFeature, GreenFeature))),
          'cb (generatedMatrixB(Seq(WhiteFabric, GreyFabric, RedFeature, BlueFeature, GreenFeature)))
        )
      }

    }

  }

  "consume" when {

    val msg = MergeAndEliminateLeafMessage(
      generatedMatrixA(Seq(fill(0), entry(2, 1)(3))),
      generatedMatrixB(Seq(fill(0), entry(2, 1)(5)))
    )

    "something" in {
      val dstElement = elementBoundTo(TestMesh, Parent)(
        generatedMatrixA(Seq(fill(1), entry(2, 3)(-1), entry(0, 5)(-2))),
        generatedMatrixB(Seq(entry(0, 0)(1), entry(1, 1)(3), entry(3, 5)(7), entry(2, 8)(15))),
        generatedMatrixX(Seq(index))
      )

      MergeAndEliminateLeaf.consume(dstElement, msg)

      dstElement should have(
        'mA (matrixA(
          +01.00, +01.00, +04.00, +00.00, +01.00, -01.00,
          +00.00, +00.00, -03.00, +01.00, +00.00, +01.00,
          +00.00, +00.00, -03.00, +01.00, +00.00, +01.00,
          +00.00, +00.00, -03.00, +01.00, +00.00, +01.00,
          +00.00, +00.00, -03.00, +01.00, +00.00, +01.00,
          +01.00, +01.00, +01.00, +01.00, +01.00, +01.00
        )),
        'mB (matrixB(
          +00.00, +05.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +15.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +01.00, -05.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, -15.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, -02.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, -15.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, -05.00, +00.00, +00.00, +00.00, +07.00, +00.00, +00.00, -15.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, -05.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, -15.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
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
