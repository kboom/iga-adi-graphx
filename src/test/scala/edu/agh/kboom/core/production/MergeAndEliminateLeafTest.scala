package edu.agh.kboom.core.production

import edu.agh.kboom.MatrixUtils.{dummyAMatrix, dummyBMatrix}
import edu.agh.kboom.core.tree._
import edu.agh.kboom.core.{IgaContext, IgaTaskContext, Mesh}
import edu.agh.kboom.{ElementUtils, ExecutionContext, SubjectSpec}

class MergeAndEliminateLeafTest extends SubjectSpec {

  val ProblemSize = 12
  val SrcSeed = 6
  val DstSeed = 9

  implicit val TestMesh: Mesh = Mesh(ProblemSize, ProblemSize, ProblemSize, ProblemSize)
  implicit val TestTree: ProblemTree = ProblemTree(ProblemSize)
  implicit val IgaTestContext: IgaContext = IgaContext(TestMesh, _ + _)
  implicit val TaskTestContext: IgaTaskContext = IgaTaskContext(14, ExecutionContext(), TestTree, IgaTestContext)

  trait SpecContext {

  }

  "MergeAndEliminateLeaf" when {

    val dstElement = ElementUtils.dummyBoundElement(BranchVertex(4), DstSeed)

    "emit applied on left child" should {

      val srcElement = ElementUtils.dummyBoundElement(LeafVertex(8), SrcSeed)

      "emits unchanged matrices" in new SpecContext {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldBe Some(MergeAndEliminateLeafMessage(
          dummyAMatrix(SrcSeed),
          dummyBMatrix(SrcSeed)
        ))
      }

    }

    "emit applied on middle child" should {

      val srcElement = ElementUtils.dummyBoundElement(LeafVertex(9), SrcSeed)

      "emits unchanged matrices" in new SpecContext {
        MergeAndEliminateLeaf.emit(srcElement, dstElement) shouldBe Some(MergeAndEliminateLeafMessage(
          dummyAMatrix(SrcSeed),
          dummyBMatrix(SrcSeed)
        ))
      }

    }

  }

}
