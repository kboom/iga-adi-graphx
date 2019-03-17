package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.core.tree.{ProblemTree, Vertex}
import org.apache.spark.graphx.{PartitionID, PartitionStrategy, VertexId}

/**
  * Collocates edges with same destination vertices.
  */
case class IgaPartitioner(tree: ProblemTree) extends PartitionStrategy {
  val mixingPrime: VertexId = 1125899906842597L

  override def getPartition(src: VertexId, dst: VertexId, numParts: PartitionID): PartitionID = {
    val dstVertex = Vertex.vertexOf(dst.toInt)(tree)
    val segments = Math.max(1, Vertex.strengthOf(dstVertex)(tree) / numParts)
    val offset = Vertex.offsetLeft(dstVertex)(tree)

    math.floor(offset / segments).toInt
  }
}
