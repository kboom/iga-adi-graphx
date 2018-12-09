package edu.agh.kboom.core.production

import edu.agh.kboom.core.IgaTaskContext
import edu.agh.kboom.core.tree.IgaElement

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

case object KeepAliveMessage extends ProductionMessage {
  override val production: Production = KeepAliveProduction
}

case object KeepAliveProduction extends Production
