package edu.agh.kboom.iga.adi.graph.solver

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

import breeze.linalg.DenseVector
import edu.agh.kboom.iga.adi.graph.solver.DirectionSolver.Log
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.LeafInitializer
import edu.agh.kboom.iga.adi.graph.solver.core.production.{InitialMessage, ProductionMessage}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.{firstIndexOfBranchingRow, lastIndexOfBranchingRow}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{Element, IgaElement, ProblemTree, Vertex}
import edu.agh.kboom.iga.adi.graph.{TimeEvent, TimeRecorder, VertexProgram}
import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, EdgeDirection, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel.MEMORY_ONLY
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import scala.concurrent._
import scala.concurrent.duration._

object DirectionSolver {
  private val Log = LoggerFactory.getLogger(classOf[IterativeSolver])
}

case class DirectionSolver(mesh: Mesh) {

  def solve(ctx: IgaContext, initializer: LeafInitializer, rec: TimeRecorder)(implicit sc: SparkContext): SplineSurface = {
    val pool: ExecutorService = Executors.newFixedThreadPool(2)
    implicit val xc: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(pool)
    val problemTree = ctx.tree()
    val partitioner = VertexPartitioner(sc.defaultParallelism, problemTree)

    val edges: RDD[Edge[IgaOperation]] =
      sc.parallelize(
        IgaTasks.generateOperations(problemTree)
          .map(e => Edge(e.src.id, e.dst.id, e))
      ).map(edge => (edge.dstId, edge))
        .partitionBy(partitioner)
        .mapPartitions({
          _.map { case (_, edge) => edge }
        }, preservesPartitioning = true) // added preserves partitioning, not sure if it helps
        .setName("Operation edges")
        .cache()
//        .localCheckpoint()

    val vertices: RDD[(VertexId, IgaElement)] =
      sc.parallelize(
        (1 to mesh.totalNodes).map(x => (x.asInstanceOf[VertexId], None))
      ).leftOuterJoin(initializer.leafData(ctx), partitioner)
      .setName("Vertices")
        .mapPartitions(
          _.map { case (v, e) =>
            val vertex = Vertex.vertexOf(v)(problemTree)
            val element = e._2.map(IgaElement(vertex, _))
              .getOrElse(IgaElement(vertex, Element.createForX(mesh)))
            (v, element)
          }
        ).cache()

    val vertexInitialisation = Future { vertices.isEmpty() }
    val edgeInitialisation = Future { edges.isEmpty() }
    Await.result(Future.sequence(Seq(vertexInitialisation, edgeInitialisation)), Duration(365, DAYS))
    pool.shutdown()

    rec.record(TimeEvent.initialized(ctx.direction))

    val solvedGraph = execute(Graph(vertices.localCheckpoint(), edges.localCheckpoint()))(ctx)
    val solutionRows = extractSolutionRows(problemTree, solvedGraph).localCheckpoint()
//    if (!solutionRows.isEmpty()) {
//      Log.info("Trigger checkpoint")
//    }

    solvedGraph.unpersist()
    vertices.unpersist()
    edges.unpersist()
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
        },
        preservesPartitioning = true
      )
  }
}
