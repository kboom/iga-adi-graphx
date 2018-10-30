package edu.agh.kboom

import edu.agh.kboom.production._
import edu.agh.kboom.tree.{BoundElement, Element, Vertex}
import org.apache.spark.graphx.{EdgeTriplet, VertexId}

case class VertexProgram(ctx: IgaContext)

object VertexProgram {

  def sendMsg(t: EdgeTriplet[Element, IgaOperation])(implicit program: VertexProgram): Iterator[(VertexId, ProductionMessage)] = {
    implicit val taskCtx: IgaTaskContext = IgaTaskContext.create(t.srcId)
    val element = BoundElement(Vertex.vertexOf(taskCtx.vid)(taskCtx.mc.xTree()), t.srcAttr)

    println(s"[$taskCtx] Sending messages from (${t.srcId}) for production (${t.attr.production})")

    t.attr.production match {
      case p: MergeAndEliminateLeaf => p.send(element)(taskCtx).map((t.dstId, _)).iterator
    }
  }

  def mergeMsg(a: ProductionMessage, b: ProductionMessage)(implicit program: VertexProgram): ProductionMessage = {
    println(s"Merging messages from ($a) and ($b)")
    a.production match {
      case p: MergeAndEliminateLeaf => p.merge(a.asInstanceOf[MergeAndEliminateLeafMessage], b.asInstanceOf[MergeAndEliminateLeafMessage])
    }
  }

  def run(id: VertexId, e: Element, m: ProductionMessage)(implicit program: VertexProgram): Element = {
    implicit val taskCtx: IgaTaskContext = IgaTaskContext.create(id)
    println(s"[$taskCtx] Running on ($id) and element ($e) production ($m)")
    ProductionExecutor.run(m.production, e)
    println(
      s"""
[$taskCtx] Finished on ($id) and element ($e) production ($m)
${Element.print(e)}
    """.stripMargin)
    e
  }

}
