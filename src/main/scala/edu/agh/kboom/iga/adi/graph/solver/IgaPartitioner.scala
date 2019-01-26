package edu.agh.kboom.iga.adi.graph.solver

import org.apache.spark.graphx.{PartitionID, PartitionStrategy, VertexId}

/**
  * Collocates edges with same destination vertices.
  */
object IgaPartitioner extends PartitionStrategy {
  override def getPartition(src: VertexId, dst: VertexId, numParts: PartitionID): PartitionID = {
    val mixingPrime: VertexId = 1125899906842597L
    (math.abs(dst * mixingPrime) % numParts).toInt
  }
}
