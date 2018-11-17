package edu.agh.kboom

import edu.agh.kboom.production.{BaseProduction, MergeAndEliminateLeaf, MergeAndEliminateLeafMessage, ProductionMessage}
import edu.agh.kboom.tree.{BoundElement, Element, Vertex}

object IgaTaskExecutor {

  def sendMessage(op: IgaOperation)(src: Element, dst: Element)(implicit taskCtx: IgaTaskContext): Option[ProductionMessage] = {
    println(s"[$taskCtx] Sending messages from (${op.src}) to (${op.dst}) for production (${t.attr.production})")
    op.p.asInstanceOf[BaseProduction].emit(BoundElement(op.src, src), BoundElement(op.dst, dst))
  }

  def mergeMessages(a: ProductionMessage, b: ProductionMessage): ProductionMessage = {
    println(s"Merging messages from ($a) and ($b)")
    a.production match {
      case MergeAndEliminateLeaf => MergeAndEliminateLeaf.merge(
        a.asInstanceOf[MergeAndEliminateLeafMessage],
        b.asInstanceOf[MergeAndEliminateLeafMessage]
      )
    }
  }

  def receiveMessage(e: Element, m: ProductionMessage)(implicit taskCtx: IgaTaskContext): Unit = {
    println(s"[$taskCtx] Running on (${taskCtx.vid}) and element ($e) production ($m)")
    val element = BoundElement(Vertex.vertexOf(taskCtx.vid)(taskCtx.mc.xTree()), e)

    m.production match {
      case MergeAndEliminateLeaf => MergeAndEliminateLeaf.consume(element, m.asInstanceOf[MergeAndEliminateLeafMessage])
    }

    println(
      s"""
[$taskCtx] Finished on ($taskCtx.vid) and element ($e) production ($m)
${Element.print(e)}
    """.stripMargin)
  }

}
