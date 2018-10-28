package edu.agh.kboom.production

import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.tree._

abstract class Production

case class MergeAndEliminateRoot() extends Production

case class BackwardsSubstituteRoot() extends Production

case class MergeAndEliminateBranch() extends Production

case class MergeAndEliminateLeaf() extends Production

case class MergeAndEliminateInterim() extends Production

case class BackwardsSubstituteInterim() extends Production

case class BackwardsSubstituteBranch() extends Production

case class BackwardsSubstituteLeaves() extends Production


object Production {

  def run(p: Production, e: Element)(implicit ctx: IgaTaskContext): Unit =
    doRun(p, BoundElement(Vertex.vertexOf(ctx.vid)(ctx.mc.xTree()), e))

  def runOnLeaf(p: Production, e: BoundElement)(implicit ctx: IgaTaskContext): Unit = p match {
    case s: InitializeLeaf => InitializeLeaf.run(s, e)
  }

  private def doRun(p: Production, e: BoundElement)(implicit ctx: IgaTaskContext): Unit = e.v match {
    case LeafVertex(_) => runOnLeaf(p, e)
    case BranchVertex(_) => Unit
    case InterimVertex(_) => Unit
  }


}