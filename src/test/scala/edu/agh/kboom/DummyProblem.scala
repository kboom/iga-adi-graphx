package edu.agh.kboom

import edu.agh.kboom.core.{IgaContext, IgaTaskContext, Mesh}
import edu.agh.kboom.core.tree.{BranchVertex, LeafVertex, ProblemTree}

trait DummyProblem {

  val ProblemSize = 12

  implicit val TestMesh: Mesh = Mesh(ProblemSize, ProblemSize, ProblemSize, ProblemSize)
  implicit val TestTree: ProblemTree = ProblemTree(ProblemSize)
  implicit val IgaTestContext: IgaContext = IgaContext(TestMesh, _ + _)
  implicit val TaskTestContext: IgaTaskContext = IgaTaskContext(14, ExecutionContext(), TestTree, IgaTestContext)

}
