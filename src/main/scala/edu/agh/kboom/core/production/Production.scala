package edu.agh.kboom.core.production

import edu.agh.kboom.core.IgaTaskContext
import edu.agh.kboom.core.tree.BoundElement

trait Production

trait BaseProduction[MSG <: ProductionMessage] {
  def emit(src: BoundElement, dst: BoundElement)(implicit ctx: IgaTaskContext): Option[MSG] = None

  def consume(dst: BoundElement, msg: MSG)(implicit ctx: IgaTaskContext): Unit
}

trait MergingProduction[MSG <: ProductionMessage] {
  def merge(a: MSG, b: MSG): MSG
}

trait ProductionMessage {
  val production: Production
}
