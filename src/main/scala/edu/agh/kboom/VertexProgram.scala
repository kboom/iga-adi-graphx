package edu.agh.kboom

import org.apache.spark.graphx.{EdgeTriplet, VertexId}

case class VertexProgram()

object VertexProgram {

  def sendMsg(t: EdgeTriplet[Node, Int])(implicit program: VertexProgram): Iterator[(VertexId, Node)] = Iterator((t.dstId, t.srcAttr))

  def mergeMsg(a: Node, b: Node)(implicit program: VertexProgram): Node = Node()

  def run(id: VertexId, vertex: Node, message: Node)(implicit program: VertexProgram): Node = Node()

}
