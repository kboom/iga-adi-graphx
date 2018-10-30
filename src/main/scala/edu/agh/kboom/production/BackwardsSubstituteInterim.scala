package edu.agh.kboom.production
import edu.agh.kboom.IgaTaskContext
import edu.agh.kboom.tree.{BoundElement, Vertex}

sealed case class BackwardsSubstituteInterimMessage()


/*

node = partial_backward_substitution(node, 1, 5, mesh.getDofsY)
        swapDofs(1, 2, 5, mesh.getDofsY)
        swapDofs(2, 3, 5, mesh.getDofsY)
        return node

 */

/**
  * BS_1_5
  */
sealed case class BackwardsSubstituteInterim() extends Production[BackwardsSubstituteInterimMessage] {
  override def prepare(src: BoundElement)(implicit ctx: IgaTaskContext): Unit = ???

  override def send(src: BoundElement)(implicit ctx: IgaTaskContext): Seq[BackwardsSubstituteInterimMessage] = ???

  override def receive(dst: BoundElement, msg: BackwardsSubstituteInterimMessage)(implicit ctx: IgaTaskContext): Unit = ???
}
