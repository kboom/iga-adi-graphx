package edu.agh.kboom

import org.apache.spark.graphx.{EdgeTriplet, VertexId}

object VertexProgram {

  def sendMsg(t: EdgeTriplet[Node, Int]): Iterator[(VertexId, Node)] = Iterator((t.dstId, t.srcAttr))

  def mergeMsg(a: Node, b: Node): Node = Node()

  def run(id: VertexId, vertex: Node, message: Node): Node = Node()

}
