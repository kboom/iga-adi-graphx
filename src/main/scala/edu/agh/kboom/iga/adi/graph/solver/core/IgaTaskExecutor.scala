package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.iga.adi.graph.solver.SolverConfig.LoadedSolverConfig
import edu.agh.kboom.iga.adi.graph.solver.core.production._
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import org.slf4j.LoggerFactory

object IgaTaskExecutor {

  private val Log = LoggerFactory.getLogger(getClass)

  val LoggingConfig = LoadedSolverConfig.logging

  def sendMessage(op: IgaOperation)(src: IgaElement, dst: IgaElement)(implicit taskCtx: IgaTaskContext): Option[ProductionMessage] = {
    if (LoggingConfig.operations) {
      Log.trace(s"[$taskCtx] ${op.p}: (${op.src})/(${src.p}) => (${op.dst})/(${dst.p}): Determining messages")
    }
    if (src.hasMorePressureThan(dst)) {
      if (LoggingConfig.operations) {
        Log.info(s"[$taskCtx] ${op.p}: (${op.src})/(${src.p}) => (${op.dst})/(${dst.p}): Sending messages")
      }
      op.p.asInstanceOf[BaseProduction[ProductionMessage]].emit(src, dst)
    } else {
      None
    }
  }

  def mergeMessages(a: ProductionMessage, b: ProductionMessage): ProductionMessage = {
    if (LoggingConfig.operations) {
      Log.trace(s"Merging messages from ($a) and ($b)")
    }
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
    }
  }

  def receiveMessage(e: IgaElement, m: ProductionMessage)(implicit taskCtx: IgaTaskContext): IgaElement = {
    val vertex = Vertex.vertexOf(taskCtx.vid)(taskCtx.mc.xTree())

    if (LoggingConfig.operations) {
      Log.info(s"Running ${m.production} on ${e.v}/(${e.p})")
    }

    m.production match {
      case ActivateVertex => if (vertex.isInstanceOf[LeafVertex]) {
        IgaElement.copy(e)
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
    }

    if (LoggingConfig.elements) {
      Log.debug(
        s"""
[$taskCtx] Run on ($taskCtx.vid) and element ($e) production (${m.production.getClass.getTypeName}))
${IgaElement.print(e)}
    """.stripMargin)
    }

    if (m.production eq MergeAndEliminateRoot) {
      e.withIncreasedPressure().withIncreasedPressure()
    } else {
      e.withIncreasedPressure()
    }
  }

}
