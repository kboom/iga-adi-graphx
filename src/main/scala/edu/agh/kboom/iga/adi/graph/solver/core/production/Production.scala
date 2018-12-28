package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.iga.adi.graph.solver.core.IgaTaskContext
import edu.agh.kboom.iga.adi.graph.solver.core.tree.IgaElement

trait Production extends Serializable

trait BaseProduction[MSG <: ProductionMessage] {
  def emit(src: IgaElement, dst: IgaElement)(implicit ctx: IgaTaskContext): Option[MSG] = None

  def consume(dst: IgaElement, msg: MSG)(implicit ctx: IgaTaskContext): Unit
}

trait MergingProduction[MSG <: ProductionMessage] {
  def merge(a: MSG, b: MSG): MSG
}

trait ProductionMessage extends Serializable {
  val production: Production
}

case object InitialMessage extends ProductionMessage {
  override val production: Production = ActivateVertex
}

case object ActivateVertex extends Production
