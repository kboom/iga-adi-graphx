package edu.agh.kboom

import edu.agh.kboom.production.Production
import edu.agh.kboom.tree.Element
import org.apache.spark.graphx.{EdgeTriplet, VertexId}

case class VertexProgram(ctx: IgaContext)

object VertexProgram {

  def sendMsg(t: EdgeTriplet[Element, IgaOperation])(implicit program: VertexProgram): Iterator[(VertexId, IgaMessage)] = Iterator.empty //Iterator((t.dstId, IgaMessage(Ay())))

  def mergeMsg(a: IgaMessage, b: IgaMessage)(implicit program: VertexProgram): IgaMessage = a

  def run(id: VertexId, e: Element, m: IgaMessage)(implicit program: VertexProgram): Element = {
    implicit val taskCtx: IgaTaskContext = IgaTaskContext.create(id)
    println(s"[$taskCtx] Running on ($id) and element ($e) production ($m)")
    Production.run(m.production, e)
    println(s"""
[$taskCtx] Finished on ($id) and element ($e) production ($m)
${Element.print(e)}
    """.stripMargin)
    e
  }

}
