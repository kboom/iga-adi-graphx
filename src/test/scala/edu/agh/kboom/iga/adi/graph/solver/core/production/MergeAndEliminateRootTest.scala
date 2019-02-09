package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.ElementUtils.elementBoundTo
import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom._
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{InterimVertex, RootVertex}

class MergeAndEliminateRootTest extends SubjectSpec
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
        MergeAndEliminateRoot.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        MergeAndEliminateRoot.emit(srcElement, dstElement).map(_.ca).map(MatrixUtils.exploded).get shouldBe fromVector(6, 6)(
          +02.02, +02.03, +02.04, +02.05, +00.00, +00.00,
          +03.02, +03.03, +03.04, +03.05, +00.00, +00.00,
          +04.02, +04.03, +04.04, +04.05, +00.00, +00.00,
          +05.02, +05.03, +05.04, +05.05, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
        )
      }

      "emits matrix B translated by one row" in {
        MergeAndEliminateRoot.emit(srcElement, dstElement).map(_.cb).map(MatrixUtils.exploded).get shouldBe fromVector(6, 14)(
          +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
          +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
          +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
          +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
        )
      }

    }

    "applied on right child" should {

      val srcElement = elementBoundTo(TestMesh, RightChild)(
        generatedMatrixA(Seq(index))
      )

      "is not empty" in {
        MergeAndEliminateRoot.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        MergeAndEliminateRoot.emit(srcElement, dstElement).map(_.ca).map(MatrixUtils.exploded).get shouldBe fromVector(6, 6)(
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +02.02, +02.03, +02.04, +02.05,
          +00.00, +00.00, +03.02, +03.03, +03.04, +03.05,
          +00.00, +00.00, +04.02, +04.03, +04.04, +04.05,
          +00.00, +00.00, +05.02, +05.03, +05.04, +05.05
        )
      }

      "emits matrix B translated by one row" in {
        MergeAndEliminateRoot.emit(srcElement, dstElement).map(_.cb).map(MatrixUtils.exploded).get shouldBe fromVector(6, 14)(
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
          +03.00, +03.01, +03.02, +03.03, +03.04, +03.05, +03.06, +03.07, +03.08, +03.09, +03.10, +03.11, +03.12, +03.13,
          +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
          +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13
        )
      }

    }

  }

  "merge" when {

    val redMsg = MergeAndEliminateRootMessage(
      generatedMatrixA(Seq(WhiteFabric, RedFeature)),
      generatedMatrixB(Seq(GreyFabric, RedFeature))
    )
    val greenMsg = MergeAndEliminateRootMessage(
      generatedMatrixA(Seq(GreyFabric, BlueFeature)),
      generatedMatrixB(Seq(WhiteFabric, BlueFeature))
    )

    "two messages" should {

      "produce a sum of matrices" in {
        MergeAndEliminateRoot.merge(redMsg, greenMsg) shouldBe MergeAndEliminateRootMessage(
          generatedMatrixA(Seq(WhiteFabric, GreyFabric, RedFeature, BlueFeature)),
          generatedMatrixB(Seq(WhiteFabric, GreyFabric, RedFeature, BlueFeature))
        )
      }

    }

  }

  "consume" when {

    val msg = MergeAndEliminateRootMessage(
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
        generatedMatrixX(Seq(fill(1), entry(0, 0)(2), entry(4, 10)(2)))
      )

      MergeAndEliminateRoot.consume(dstElement, msg)

      ElementUtils.weakPrecision(dstElement) should have(
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
          +05.73, -02.50, +00.00, +00.00, +00.00, +00.00, +02.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          -02.25, +00.00, +00.00, +00.00, +00.00, +00.00, +04.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          +01.88, +02.50, +00.00, +00.00, +00.00, +00.00, -06.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          -01.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          -00.75, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
          -00.60, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
        ))
      )
    }

  }

}
