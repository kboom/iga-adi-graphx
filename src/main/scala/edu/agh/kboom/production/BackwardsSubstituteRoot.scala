package edu.agh.kboom.production
import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.tree.BoundElement

case class BackwardsSubstituteRoot() extends Production {
  override def run(e: BoundElement)(implicit ctx: IgaTaskContext): Unit = ???
}
