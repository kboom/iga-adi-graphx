package edu.agh.kboom.iga.adi.graph.solver

import breeze.linalg.DenseVector
import edu.agh.kboom.iga.adi.graph.solver.DirectionSolver.Log
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.LeafInitializer
import edu.agh.kboom.iga.adi.graph.solver.core.production.{InitialMessage, ProductionMessage}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.{firstIndexOfBranchingRow, lastIndexOfBranchingRow}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{Element, IgaElement, ProblemTree, Vertex}
import edu.agh.kboom.iga.adi.graph.{TimeEvent, TimeRecorder, VertexProgram}
import org.apache.spark.{HashPartitioner, SparkContext}
import org.apache.spark.graphx.{Edge, EdgeDirection, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel.MEMORY_ONLY
import org.slf4j.LoggerFactory

object DirectionSolver {
  private val Log = LoggerFactory.getLogger(classOf[IterativeSolver])
}

case class DirectionSolver(mesh: Mesh) {

  def solve(ctx: IgaContext, initializer: LeafInitializer, rec: TimeRecorder)(implicit sc: SparkContext): SplineSurface = {
    val problemTree = ctx.tree()

    val edges: RDD[Edge[IgaOperation]] =
      sc.parallelize(
        IgaTasks.generateOperations(problemTree)
          .map(e => Edge(e.src.id, e.dst.id, e))
      ).setName("Operation edges")

    val graph = Graph.fromEdges(edges, None, MEMORY_ONLY, MEMORY_ONLY)
      .partitionBy(IgaPartitioner(problemTree)) // todo create an efficient partitioner for IGA-ADI operations
      .mapVertices((vid, _) => IgaElement(Vertex.vertexOf(vid.toInt)(problemTree), Element.createForX(mesh)))
      .joinVertices(initializer.leafData(ctx))((_, v, se) => v.swapElement(se))
      .cache() // todo is this really necessary? It greatly reduces the available memory and might not be needed at all

    graph.edges.isEmpty()
    graph.vertices.isEmpty() // extremely important for performance (for some reason)

    rec.record(TimeEvent.initialized(ctx.direction))

    val solvedGraph = execute(graph)(ctx)
    val solutionRows = extractSolutionRows(problemTree, solvedGraph).localCheckpoint() // forget about lineage
    if (!solutionRows.isEmpty()) {
      Log.info("Trigger checkpoint")
    }

    solvedGraph.unpersist(blocking = false)
    graph.unpersist(blocking = false)
    SplineSurface(solutionRows, mesh)
  }

  private def execute(dataItemGraph: Graph[IgaElement, IgaOperation])(implicit igaContext: IgaContext) = {
    implicit val program: VertexProgram = VertexProgram(igaContext)
    dataItemGraph.pregel(InitialMessage.asInstanceOf[ProductionMessage], activeDirection = EdgeDirection.Out)(
      VertexProgram.run,
      VertexProgram.sendMsg,
      VertexProgram.mergeMsg
    )
  }

  private def extractSolutionRows(problemTree: ProblemTree, result: Graph[IgaElement, IgaOperation]): RDD[(Long, DenseVector[Double])] = {
    val firstIndex = firstIndexOfBranchingRow(problemTree)
    val lastIndex = lastIndexOfBranchingRow(problemTree)

    result.vertices
      .filterByRange(firstIndex, lastIndex)
      .mapPartitions(
        _.map { case (v, be) =>
          val local = v - firstIndex
          if (local == 0) (local, be.e.mX(0 to -2, ::)) else (local, be.e.mX(2 to -2, ::))
        }.flatMap { case (vid, be) => (0 until be.rows).view.map(be(_, ::).inner.copy)
          .zipWithIndex
          .map { case (v, i) => if (vid == 0) (i.toLong, v) else (5 + (vid - 1) * 3 + i, v) }
        }
      )
  }
}
