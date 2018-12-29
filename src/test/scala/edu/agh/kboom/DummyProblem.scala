package edu.agh.kboom

import edu.agh.kboom.iga.adi.graph.ExecutionContext
import edu.agh.kboom.iga.adi.graph.problems.OneProblem
import edu.agh.kboom.iga.adi.graph.solver.IgaContext
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaTaskContext, Mesh}

trait DummyProblem {

  val ProblemSize = 12

  implicit val TestMesh: Mesh = Mesh(ProblemSize, ProblemSize, ProblemSize, ProblemSize)
  implicit val TestTree: ProblemTree = ProblemTree(ProblemSize)
  implicit val IgaTestContext: IgaContext = IgaContext(TestMesh, OneProblem)
  implicit val TaskTestContext: IgaTaskContext = IgaTaskContext(14, ExecutionContext(), TestTree, IgaTestContext)

}
