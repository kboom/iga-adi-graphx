package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.VertexProgram
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.LeafInitializer
import edu.agh.kboom.iga.adi.graph.solver.core.production.{InitialMessage, ProductionMessage}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.{firstIndexOfBranchingRow, lastIndexOfBranchingRow}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{Element, IgaElement, ProblemTree, Vertex}
import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, EdgeDirection, Graph}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}
import org.apache.spark.rdd.RDD

case class DirectionSolver(mesh: Mesh) {

  def solve(ctx: IgaContext, initializer: LeafInitializer)(implicit sc: SparkContext): Solution = {
    val problemTree = ctx.tree()

    val edges: RDD[Edge[IgaOperation]] =
      sc.parallelize(
        IgaTasks.generateOperations(problemTree)
          .map(e => Edge(e.src.id, e.dst.id, e))
      )

    val dataItemGraph = Graph.fromEdges(edges, None)
      .mapVertices((vid, _) => IgaElement(Vertex.vertexOf(vid.toInt)(problemTree), Element.createForX(mesh)))
      .joinVertices(initializer.leafData(ctx))((_, v, se) => v.swapElement(se))

    val result = execute(dataItemGraph)(ctx)

    val hs = extractSolution(problemTree, result)

//    dataItemGraph.unpersist(blocking = false)

    Solution(hs, mesh)
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
