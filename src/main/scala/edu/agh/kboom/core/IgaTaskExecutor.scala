package edu.agh.kboom.core

import edu.agh.kboom.core.production._
import edu.agh.kboom.core.production.initialisation.InitializeLeafAlongX
import edu.agh.kboom.core.tree._
import org.slf4j.LoggerFactory

object IgaTaskExecutor {

  private val Log = LoggerFactory.getLogger(getClass)

  def sendMessage(op: IgaOperation)(src: IgaElement, dst: IgaElement)(implicit taskCtx: IgaTaskContext): Option[ProductionMessage] = {
    Log.trace(s"[$taskCtx] ${op.p}: (${op.src})/(${src.p}) => (${op.dst})/(${dst.p}): Determining messages")
    if(src.hasMorePressureThan(dst)) {
      Log.debug(s"[$taskCtx] ${op.p}: (${op.src})/(${src.p}) => (${op.dst})/(${dst.p}): Sending messages")
      op.p.asInstanceOf[BaseProduction[ProductionMessage]].emit(src, dst)
    } else {
//      Some(KeepAliveMessage)
      None
    }
  }

  def mergeMessages(a: ProductionMessage, b: ProductionMessage): ProductionMessage = {
    Log.trace(s"Merging messages from ($a) and ($b)")
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
      case MergeAndEliminateRoot => MergeAndEliminateRoot.merge(
        a.asInstanceOf[MergeAndEliminateRootMessage],
        b.asInstanceOf[MergeAndEliminateRootMessage]
      )
      case KeepAliveProduction => a
    }
  }

  def receiveMessage(e: IgaElement, m: ProductionMessage)(implicit taskCtx: IgaTaskContext): IgaElement = {
    val vertex = Vertex.vertexOf(taskCtx.vid)(taskCtx.mc.xTree())

    Log.trace(s"Running ${m.production} on ${e.v}/(${e.p})")

    m.production match {
      case InitializeLeafAlongX => if(vertex.isInstanceOf[LeafVertex]) {
        InitializeLeafAlongX.initialize(e)
      } else return e
      case MergeAndEliminateLeaf =>
        MergeAndEliminateLeaf.consume(e, m.asInstanceOf[MergeAndEliminateLeafMessage])
      case MergeAndEliminateBranch =>
        MergeAndEliminateBranch.consume(e, m.asInstanceOf[MergeAndEliminateBranchMessage])
      case MergeAndEliminateInterim =>
        MergeAndEliminateInterim.consume(e, m.asInstanceOf[MergeAndEliminateInterimMessage])
      case MergeAndEliminateRoot =>
        MergeAndEliminateRoot.consume(e, m.asInstanceOf[MergeAndEliminateRootMessage])
      case BackwardsSubstituteRoot =>
        BackwardsSubstituteRoot.consume(e, m.asInstanceOf[BackwardsSubstituteRootMessage])
      case BackwardsSubstituteInterim =>
        BackwardsSubstituteInterim.consume(e, m.asInstanceOf[BackwardsSubstituteInterimMessage])
      case BackwardsSubstituteBranch =>
        BackwardsSubstituteBranch.consume(e, m.asInstanceOf[BackwardsSubstituteBranchMessage])
      case KeepAliveProduction => return e
    }

    Log.debug(
      s"""
[$taskCtx] Run on ($taskCtx.vid) and element ($e) production (${m.production.getClass.getTypeName}))
${IgaElement.print(e)}
    """.stripMargin)

    if(m.production eq MergeAndEliminateRoot) {
      e.withIncreasedPressure().withIncreasedPressure()
    } else {
      e.withIncreasedPressure()
    }
  }

}
