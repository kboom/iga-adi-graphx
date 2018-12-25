package edu.agh.kboom.core

import edu.agh.kboom.VertexProgram
import edu.agh.kboom.core.initialisation.LeafInitializer
import edu.agh.kboom.core.production.{InitialMessage, ProductionMessage}
import edu.agh.kboom.core.tree.ProblemTree.{firstIndexOfBranchingRow, lastIndexOfBranchingRow}
import edu.agh.kboom.core.tree.{Element, IgaElement, ProblemTree, Vertex}
import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, EdgeDirection, Graph}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}
import org.apache.spark.rdd.RDD

case class DirectionSolver(mesh: Mesh) {

  def solve(problem: Problem, initializer: LeafInitializer)(implicit sc: SparkContext): Solution = {
    implicit val problemTree = ProblemTree(mesh.xSize)
    implicit val igaContext = IgaContext(mesh, problem.valueAt)

    val edges: RDD[Edge[IgaOperation]] =
      sc.parallelize(
        IgaTasks.generateOperations(problemTree)
          .map(e => Edge(e.src.id, e.dst.id, e))
      )

    val dataItemGraph = Graph.fromEdges(edges, None)
      .mapVertices((vid, _) => IgaElement(Vertex.vertexOf(vid.toInt)(problemTree), Element.createForX(mesh)))
      .joinVertices(initializer.leafData(igaContext))((_, v, se) => v.swapElement(se))

    val result = execute(dataItemGraph)

    val hs = extractSolution(problemTree, result)

    Solution(hs)
  }

  private def execute(dataItemGraph: Graph[IgaElement, IgaOperation])(implicit igaContext: IgaContext) = {
    implicit val program: VertexProgram = VertexProgram(igaContext)
    dataItemGraph.pregel(InitialMessage.asInstanceOf[ProductionMessage], activeDirection = EdgeDirection.Out)(
      VertexProgram.run,
      VertexProgram.sendMsg,
      VertexProgram.mergeMsg
    )
  }

  private def extractSolution(problemTree: ProblemTree, result: Graph[IgaElement, IgaOperation]) = {
    new IndexedRowMatrix(result.vertices
      .filterByRange(firstIndexOfBranchingRow(problemTree), lastIndexOfBranchingRow(problemTree))
      .map { case (v, e) => (v - firstIndexOfBranchingRow(problemTree), e) }
      .map { case (v, be) => if (v == 0) (v, be.e.mX.arr.dropRight(1)) else (v, be.e.mX.arr.drop(2).dropRight(1)) }
      .flatMap { case (vid, be) => be.map(Vectors.dense).zipWithIndex.map { case (v, i) => if (vid == 0) IndexedRow(i, v) else IndexedRow(5 + (vid - 1) * 3 + i, v) } }
    )
  }
}
