package edu.agh.kboom.iga.adi.graph.solver

import org.apache.spark.graphx.{PartitionID, PartitionStrategy, VertexId}

/**
  * Collocates edges with same destination vertices.
  */
object IgaPartitioner extends PartitionStrategy {
  val mixingPrime: VertexId = 1125899906842597L

  override def getPartition(src: VertexId, dst: VertexId, numParts: PartitionID): PartitionID = {
    (math.abs(dst * mixingPrime) % numParts).toInt
  }
}
