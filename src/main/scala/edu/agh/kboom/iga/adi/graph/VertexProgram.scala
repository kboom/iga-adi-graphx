package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.solver.IgaContext
import edu.agh.kboom.iga.adi.graph.solver.core.production.ProductionMessage
import edu.agh.kboom.iga.adi.graph.solver.core.tree.IgaElement
import edu.agh.kboom.iga.adi.graph.solver.core.{IgaOperation, IgaTaskContext, IgaTaskExecutor}
import org.apache.spark.graphx.{EdgeTriplet, VertexId}

sealed case class VertexProgram(ctx: IgaContext)

object VertexProgram {
  def sendMsg(t: EdgeTriplet[IgaElement, IgaOperation])(implicit program: VertexProgram): Iterator[(VertexId, ProductionMessage)] = {
    implicit val taskCtx: IgaTaskContext = IgaTaskContext.create(t.srcId)
    IgaTaskExecutor.sendMessage(t.attr)(t.srcAttr, t.dstAttr).map((t.dstId, _)).iterator
  }

  def mergeMsg(a: ProductionMessage, b: ProductionMessage)(implicit program: VertexProgram): ProductionMessage =
    IgaTaskExecutor.mergeMessages(a, b)

  def run(id: VertexId, e: IgaElement, m: ProductionMessage)(implicit program: VertexProgram): IgaElement = {
    implicit val taskCtx: IgaTaskContext = IgaTaskContext.create(id)
    IgaTaskExecutor.receiveMessage(e, m)
  }
}
