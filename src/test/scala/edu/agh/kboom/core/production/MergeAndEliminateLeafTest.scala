package edu.agh.kboom.core.production

import edu.agh.kboom.MatrixUtils.{dummyAMatrix, dummyBMatrix, fromVector, indexedSquareMatrix}
import edu.agh.kboom.core._
import edu.agh.kboom.core.tree._
import edu.agh.kboom.{ElementUtils, ExecutionContext, SubjectSpec}

class MergeAndEliminateLeafTest extends SubjectSpec {

  val ProblemSize = 12
  val SrcSeed = 6
  val DstSeed = 9

  implicit val TestMesh: Mesh = Mesh(ProblemSize, ProblemSize, ProblemSize, ProblemSize)
  implicit val TestTree: ProblemTree = ProblemTree(ProblemSize)
  implicit val IgaTestContext: IgaContext = IgaContext(TestMesh, _ + _)
  implicit val TaskTestContext: IgaTaskContext = IgaTaskContext(14, ExecutionContext(), TestTree, IgaTestContext)

  "MergeAndEliminateLeaf" when {

    val dstElement = ElementUtils.dummyBoundElement(BranchVertex(4), DstSeed)

    "emit applied on left child" should {

      val srcElement = ElementUtils.dummyBoundElement(LeafVertex(8), SrcSeed)

      "emits unchanged matrices" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldBe Some(MergeAndEliminateLeafMessage(
          dummyAMatrix(SrcSeed),
          dummyBMatrix(SrcSeed)
        ))
      }

    }

    "emit applied on middle child" should {

      val srcElement = ElementUtils.elementBoundTo(TestMesh, LeafVertex(9))(indexedSquareMatrix(6))

      "emits matrix A translated by 1,1" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).get should have(
          'ca (MatrixA(fromVector(6, 6)(
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +01.00, +02.00, +00.00, +00.00,
            +00.00, +06.00, +07.00, +08.00, +00.00, +00.00,
            +00.00, +12.00, +13.00, +14.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )))
        )
      }

      "emits matrix B translated by one row" in {
        MergeAndEliminateLeaf.emit(srcElement, dstElement).get should have(
          'cb (MatrixB(fromVector(6, 14)(
            +14.00, +15.00, +16.00, +17.00, +18.00, +19.00, +20.00, +21.00, +22.00, +23.00, +24.00, +25.00, +26.00, +27.00,
            +28.00, +29.00, +30.00, +31.00, +32.00, +33.00, +34.00, +35.00, +36.00, +37.00, +38.00, +39.00, +40.00, +41.00,
            +42.00, +43.00, +44.00, +45.00, +46.00, +47.00, +48.00, +49.00, +50.00, +51.00, +52.00, +53.00, +54.00, +55.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
            +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
          )))
        )
      }

    }


  }

}
