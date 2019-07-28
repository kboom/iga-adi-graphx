package edu.agh.kboom.iga.adi.graph.solver

import breeze.linalg.DenseVector
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.initialisation.LeafInitializer
import edu.agh.kboom.iga.adi.graph.solver.core.production.{InitialMessage, ProductionMessage}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.{firstIndexOfBranchingRow, lastIndexOfBranchingRow}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{Element, IgaElement, ProblemTree, Vertex}
import edu.agh.kboom.iga.adi.graph.spark.EvenlyDistributedRDD
import edu.agh.kboom.iga.adi.graph.{TimeEvent, TimeRecorder, VertexProgram}
import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, EdgeDirection, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel.OFF_HEAP
import org.slf4j.LoggerFactory

object DirectionSolver {
  private val Log = LoggerFactory.getLogger(classOf[IterativeSolver])
}

case class DirectionSolver(mesh: Mesh) {

  val edgesTemplate: Seq[Edge[IgaOperation]] = IgaTasks.generateOperations(ProblemTree(mesh.xSize))
    .map(e => Edge(e.src.id, e.dst.id, e))

  //  val vertexTemplate: immutable.IndexedSeq[(VertexId, None.type)] = (1 to mesh.totalNodes).map(x => (x.asInstanceOf[VertexId], None))

  def solve(ctx: IgaContext, initializer: LeafInitializer, rec: TimeRecorder)(implicit sc: SparkContext): SplineSurface = {
    val problemTree = ctx.tree()
    val partitioner = VertexPartitioner(sc.defaultParallelism, problemTree)

    val edges: RDD[Edge[IgaOperation]] =
      sc.parallelize(edgesTemplate)
        .setName("Operation edges")
        .persist(OFF_HEAP)

    val init = initializer.leafData(ctx)
      .persist(OFF_HEAP)

    val vertices: RDD[(VertexId, IgaElement)] =
      new EvenlyDistributedRDD(sc, 1, mesh.totalNodes())
        .leftOuterJoin(init, partitioner)
        .setName("Vertices")
        .mapPartitions(
          _.map { case (v, e) =>
            val vertex = Vertex.vertexOf(v)(problemTree)
            val element = e._2.map(IgaElement(vertex, _))
              .getOrElse(IgaElement(vertex, Element.createForX(mesh)))
            (v, element)
          }, preservesPartitioning = true
        ).persist(OFF_HEAP)

    vertices.count()
    edges.count()

    rec.record(TimeEvent.initialized(ctx.direction))

    //    val g = GraphImpl.fromExistingRDDs(vertices, edges)

    val graph = Graph(
      vertices = vertices,
      edges = edges,
      defaultVertexAttr = null,
      edgeStorageLevel = OFF_HEAP,
      vertexStorageLevel = OFF_HEAP
    )

    //.partitionBy(IgaPartitioner(problemTree)) // partitioner must be here, this is going to be random till we hit the partitioner

    val solvedGraph = execute(graph)(ctx)
    val solutionRows = extractSolutionRows(problemTree, solvedGraph)
      .persist(OFF_HEAP)
      .localCheckpoint()

    solutionRows.count()

    graph.unpersist(blocking = false)
    solvedGraph.unpersist(blocking = false)
    edges.unpersist(blocking = false)
    vertices.unpersist(blocking = false)
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
