package edu.agh.kboom

import edu.agh.kboom.tree.BoundElement

package object production {

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

  def swapDofs(a: Int, b: Int, size: Int, nrhs: Int)(implicit p: BoundElement): Unit = {
    for (i <- 1 to size) {
      p.mA.swap(a, i, b, i)
    }
    for (i <- 1 to size) {
      p.mA.swap(i, a, i, b)
    }
    for (i <- 1 to nrhs) {
      p.mB.swap(a, i, b, i)
      p.mB.swap(a, i, b, i)
    }
  }

  def partialForwardElimination(elim: Int, size: Int, nrhs: Int)(implicit p: BoundElement): Unit = {
    for (irow <- 1 to elim) {
      val diag = elim(irow)(irow)
      for (icol <- irow to size) {
        p.mA(irow)(icol) /= diag
      }
      for (irhs <- 1 to nrhs) {
        p.mB(irow)(irhs) /= diag
      }
      for (isub <- irow + 1 to size) {
        val mult = p.mA(isub)(irow)
        for (icol <- irow to size) {
          p.mA(isub)(icol) -= p.mA(irow)(icol) * mult
        }
        for (irhs <- 1 to nrhs) {
          p.mB(isub)(irhs) -= p.mB(irow)(irhs) * mult
        }
      }
    }
  }

  def partialBackwardsSubstitution(elim: Int, size: Int, nrhs: Int)(implicit p: BoundElement): Unit = {
    for (irhs <- 1 to nrhs) {
      for (irow <- elim to 1 by -1) {
        p.mX(irow)(irhs) = p.mB(irow)(irhs)
        for (icol <- irow + 1 to size) {
          p.mX(irow)(irhs) -= p.mA(irow)(icol) * p.mX(icol)(irhs)
        }
        p.mX(irow)(irhs) /= p.mA(irow)(irow)
      }
    }
  }

}
