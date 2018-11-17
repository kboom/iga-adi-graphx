package edu.agh.kboom

import edu.agh.kboom.core.production.ProductionMessage
import edu.agh.kboom.core.tree.Element
import edu.agh.kboom.core.{IgaContext, IgaOperation, IgaTaskContext, IgaTaskExecutor}
import org.apache.spark.graphx.{EdgeTriplet, VertexId}

case class VertexProgram(ctx: IgaContext)

object VertexProgram {
  def sendMsg(t: EdgeTriplet[Element, IgaOperation])(implicit program: VertexProgram): Iterator[(VertexId, ProductionMessage)] = {
    implicit val taskCtx: IgaTaskContext = IgaTaskContext.create(t.srcId)
    IgaTaskExecutor.sendMessage(t.attr)(t.srcAttr, t.dstAttr).map((t.dstId, _)).iterator
  }

  def mergeMsg(a: ProductionMessage, b: ProductionMessage)(implicit program: VertexProgram): ProductionMessage =
    IgaTaskExecutor.mergeMessages(a, b)

  def run(id: VertexId, e: Element, m: ProductionMessage)(implicit program: VertexProgram): Element = {
    implicit val taskCtx: IgaTaskContext = IgaTaskContext.create(id)
    IgaTaskExecutor.receiveMessage(e, m)
    e
  }
}
