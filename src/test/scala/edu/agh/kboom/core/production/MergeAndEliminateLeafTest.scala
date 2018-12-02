package edu.agh.kboom.core.production

import edu.agh.kboom.ElementUtils.featuredBoundElement
import edu.agh.kboom.MatrixUtils.{dummyAMatrix, dummyBMatrix, fromVector, indexedAMatrix, indexedBMatrix, indexedSquareMatrix, indexedXMatrix}
import edu.agh.kboom.core._
import edu.agh.kboom.core.tree._
import edu.agh.kboom.{ElementUtils, ExecutionContext, SubjectSpec}

class MergeAndEliminateLeafTest extends SubjectSpec {

  val ProblemSize = 12
  val RedFeature = 6
  val GreenFeature = 9

  val Parent = BranchVertex(4)
  val LeftChild = LeafVertex(8)
  val MiddleChild = LeafVertex(9)
  val RightChild = LeafVertex(10)

  implicit val TestMesh: Mesh = Mesh(ProblemSize, ProblemSize, ProblemSize, ProblemSize)
  implicit val TestTree: ProblemTree = ProblemTree(ProblemSize)
  implicit val IgaTestContext: IgaContext = IgaContext(TestMesh, _ + _)
  implicit val TaskTestContext: IgaTaskContext = IgaTaskContext(14, ExecutionContext(), TestTree, IgaTestContext)

  "emit" when {

    val dstElement = featuredBoundElement(Parent, GreenFeature)

    "applied on left child" should {

      val srcElement = featuredBoundElement(LeftChild, RedFeature)

      "is not empty" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits unchanged matrices" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldBe Some(MergeAndEliminateLeafMessage(
          dummyAMatrix(RedFeature),
          dummyBMatrix(RedFeature)
        ))
      }

    }

    "applied on middle child" should {

      val srcElement = ElementUtils.elementBoundTo(TestMesh, MiddleChild)(indexedSquareMatrix(6))

      "is not empty" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).get should have(
          'ca (MatrixA(fromVector(6, 6)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.01, +00.02, +00.00, +00.00,
            +00.00, +01.00, +01.01, +01.02, +00.00, +00.00,
            +00.00, +02.00, +02.01, +02.02, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )))
        )
      }

      "emits matrix B translated by one row" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).get should have(
          'cb (MatrixB(fromVector(6, 14)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
            +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
            +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )))
        )
      }

    }

    "applied on right child" should {

      val srcElement = ElementUtils.elementBoundTo(TestMesh, RightChild)(indexedSquareMatrix(6))

      "is not empty" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldNot be(empty)
      }

      "emits matrix A translated by 1,1" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).get should have(
          'ca (MatrixA(fromVector(6, 6)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.01, +00.02, +00.00,
            +00.00, +00.00, +01.00, +01.01, +01.02, +00.00,
            +00.00, +00.00, +02.00, +02.01, +02.02, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )))
        )
      }

      "emits matrix B translated by one row" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).get should have(
          'cb (MatrixB(fromVector(6, 14)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
            +01.00, +01.01, +01.02, +01.03, +01.04, +01.05, +01.06, +01.07, +01.08, +01.09, +01.10, +01.11, +01.12, +01.13,
            +02.00, +02.01, +02.02, +02.03, +02.04, +02.05, +02.06, +02.07, +02.08, +02.09, +02.10, +02.11, +02.12, +02.13,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )))
        )
      }

    }

  }

  "merge" when {

    val redMsg = MergeAndEliminateLeafMessage(dummyAMatrix(RedFeature), dummyBMatrix(RedFeature))
    val greenMsg = MergeAndEliminateLeafMessage(dummyAMatrix(GreenFeature), dummyBMatrix(GreenFeature))

    "two messages" should {

      "produce a sum of matrices" in {
        MergeAndEliminateLeaf.merge(redMsg, greenMsg) shouldBe MergeAndEliminateLeafMessage(
          dummyAMatrix(RedFeature, GreenFeature),
          dummyBMatrix(RedFeature, GreenFeature)
        )
      }

    }

  }

  "vertex" when {

    val msg = MergeAndEliminateLeafMessage(dummyAMatrix(RedFeature), dummyBMatrix(RedFeature))
    val dstElement = ElementUtils.constBoundElement(Parent, GreenFeature)

    MergeAndEliminateLeaf.consume(dstElement, msg)

    "merge and eliminate" in {
      dstElement.mA shouldBe MatrixA(fromVector(6, 6)(
        +01.00, +01.00, +01.00, +01.00, +01.00, +09.00,
        +00.00, +00.00, +00.00, +00.00, +00.00, +09.00,
        +00.00, -01.00, -01.00, -01.00, -01.00, +09.00,
        +00.00, +00.00, +00.00, +00.00, +00.00, +09.00,
        +00.00, +00.00, +00.00, +00.00, +00.00, +09.00,
        +09.00, +09.00, +09.00, +09.00, +09.00, +09.00
      ))
    }

  }

}
