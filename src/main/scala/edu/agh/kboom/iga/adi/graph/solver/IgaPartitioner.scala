package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.core.tree.{ProblemTree, Vertex}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex.{offsetLeft, strengthOf, vertexOf}
import org.apache.spark.Partitioner
import org.apache.spark.graphx.{PartitionID, PartitionStrategy, VertexId}


/**
  * Collocates edges with same destination vertices.
  */
case class IgaPartitioner(tree: ProblemTree) extends PartitionStrategy {
  val mixingPrime: VertexId = 1125899906842597L

  override def getPartition(src: VertexId, dst: VertexId, numParts: PartitionID): PartitionID = {
    if (src > dst) {
      IgaPartitioner.partitionFor(dst, numParts)(tree)
    } else {
      IgaPartitioner.partitionFor(src, numParts)(tree)
    }
  }
}

case class VertexPartitioner(numPartitions: Int, tree: ProblemTree) extends Partitioner {
  override def getPartition(key: Any): PartitionID = {
    IgaPartitioner.partitionFor(key.asInstanceOf[Int], numPartitions)(tree)
  }
}

object IgaPartitioner {

  def partitionFor(dst: VertexId, numParts: PartitionID)(implicit tree: ProblemTree): Int = {
    partitionFor(vertexOf(dst.toInt)(tree), numParts)
  }

  def partitionFor(dst: Vertex, numParts: PartitionID)(implicit tree: ProblemTree): Int = {
    val segments = Math.max(1, strengthOf(dst)(tree) / numParts)
    val offset = offsetLeft(dst)(tree)

    math.floor(offset / segments).toInt
  }
}
