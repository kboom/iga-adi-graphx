package edu.agh.kboom.core

import edu.agh.kboom.core.production._
import edu.agh.kboom.core.tree.{BoundElement, Element, LeafVertex, Vertex}

object IgaTaskExecutor {

  def sendMessage(op: IgaOperation)(src: Element, dst: Element)(implicit taskCtx: IgaTaskContext): Option[ProductionMessage] = {
    println(s"[$taskCtx] Sending messages from (${op.src}) to (${op.dst}) for production (${op.p})")
    op.p.asInstanceOf[BaseProduction].emit(BoundElement(op.src, src), BoundElement(op.dst, dst))
  }

  def mergeMessages(a: ProductionMessage, b: ProductionMessage): ProductionMessage = {
    println(s"Merging messages from ($a) and ($b)")
    a.production match {
      case MergeAndEliminateLeaf => MergeAndEliminateLeaf.merge(
        a.asInstanceOf[MergeAndEliminateLeafMessage],
        b.asInstanceOf[MergeAndEliminateLeafMessage]
      )
      case MergeAndEliminateBranch => MergeAndEliminateBranch.merge(
        a.asInstanceOf[MergeAndEliminateBranchMessage],
        b.asInstanceOf[MergeAndEliminateBranchMessage]
      )
      case MergeAndEliminateInterim => MergeAndEliminateInterim.merge(
        a.asInstanceOf[MergeAndEliminateInterimMessage],
        b.asInstanceOf[MergeAndEliminateInterimMessage]
      )
      case SolveRoot => SolveRoot.merge(
        a.asInstanceOf[SolveRootMessage],
        b.asInstanceOf[SolveRootMessage]
      )
    }
  }

  def receiveMessage(e: Element, m: ProductionMessage)(implicit taskCtx: IgaTaskContext): Unit = {
    println(s"[$taskCtx] Running on (${taskCtx.vid}) and element ($e) production ($m)")
    val vertex = Vertex.vertexOf(taskCtx.vid)(taskCtx.mc.xTree())
    val element = BoundElement(vertex, e)

    m.production match {
      case InitializeLeaf => if(vertex.isInstanceOf[LeafVertex]) InitializeLeaf.initialize(element)
      case MergeAndEliminateLeaf => MergeAndEliminateLeaf.consume(element, m.asInstanceOf[MergeAndEliminateLeafMessage])
      case MergeAndEliminateBranch => MergeAndEliminateBranch.consume(element, m.asInstanceOf[MergeAndEliminateBranchMessage])
      case MergeAndEliminateInterim => MergeAndEliminateInterim.consume(element, m.asInstanceOf[MergeAndEliminateInterimMessage])
      case SolveRoot => SolveRoot.consume(element, m.asInstanceOf[SolveRootMessage])
      case BackwardsSubstituteInterim => BackwardsSubstituteInterim.consume(element, m.asInstanceOf[BackwardsSubstituteInterimMessage])
      case BackwardsSubstituteBranch => BackwardsSubstituteBranch.consume(element, m.asInstanceOf[BackwardsSubstituteBranchMessage])
    }

    println(
      s"""
[$taskCtx] Finished on ($taskCtx.vid) and element ($e) production ($m)
${Element.print(e)}
    """.stripMargin)
  }

}
