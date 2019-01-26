package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.VertexProgram
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.LeafInitializer
import edu.agh.kboom.iga.adi.graph.solver.core.production.{InitialMessage, ProductionMessage}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.{firstIndexOfBranchingRow, lastIndexOfBranchingRow}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{Element, IgaElement, ProblemTree, Vertex}
import org.apache.spark.SparkContext
import org.apache.spark.graphx.PartitionStrategy.EdgePartition2D
import org.apache.spark.graphx.{Edge, EdgeDirection, Graph}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel.{MEMORY_AND_DISK_SER, MEMORY_ONLY}

case class DirectionSolver(mesh: Mesh) {

  def solve(ctx: IgaContext, initializer: LeafInitializer)(implicit sc: SparkContext): SplineSurface = {
    val problemTree = ctx.tree()

    val edges: RDD[Edge[IgaOperation]] =
      sc.parallelize(
        IgaTasks.generateOperations(problemTree)
          .map(e => Edge(e.src.id, e.dst.id, e))
      ).setName("Operation edges")

    val graph = Graph.fromEdges(edges, None, MEMORY_ONLY, MEMORY_ONLY)
      .partitionBy(EdgePartition2D)  // todo create an efficient partitioner for IGA-ADI operations
      .mapVertices((vid, _) => IgaElement(Vertex.vertexOf(vid.toInt)(problemTree), Element.createForX(mesh)))
      .joinVertices(initializer.leafData(ctx))((_, v, se) => v.swapElement(se))

    val solvedGraph = execute(graph)(ctx)
    val solutionRows = extractSolutionRows(problemTree, solvedGraph).localCheckpoint() // forget about lineage
    if(!solutionRows.isEmpty()) {
      println("Trigger checkpoint")
    }

    solvedGraph.unpersist()
    SplineSurface(new IndexedRowMatrix(solutionRows), mesh)
  }

  private def execute(dataItemGraph: Graph[IgaElement, IgaOperation])(implicit igaContext: IgaContext) = {
    implicit val program: VertexProgram = VertexProgram(igaContext)
    dataItemGraph.pregel(InitialMessage.asInstanceOf[ProductionMessage], activeDirection = EdgeDirection.Out)(
      VertexProgram.run,
      VertexProgram.sendMsg,
      VertexProgram.mergeMsg
    )
  }

  private def extractSolutionRows(problemTree: ProblemTree, result: Graph[IgaElement, IgaOperation]) = {
    result.vertices
      .filterByRange(firstIndexOfBranchingRow(problemTree), lastIndexOfBranchingRow(problemTree))
      .map { case (v, e) => (v - firstIndexOfBranchingRow(problemTree), e) }
      .map { case (v, be) => if (v == 0) (v, be.e.mX.arr.dropRight(1)) else (v, be.e.mX.arr.drop(2).dropRight(1)) }
      .flatMap { case (vid, be) => be.map(Vectors.dense).zipWithIndex.map { case (v, i) => if (vid == 0) IndexedRow(i, v) else IndexedRow(5 + (vid - 1) * 3 + i, v) } }
  }
}
