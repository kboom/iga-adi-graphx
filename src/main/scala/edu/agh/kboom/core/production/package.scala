package edu.agh.kboom.core

import edu.agh.kboom.core.tree.BoundElement

package object production {

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
      val diag = p.mA(irow)(irow)
      for (icol <- irow to size) {
        p.mA.replace(irow, icol)(_ / diag)
      }
      for (irhs <- 1 to nrhs) {
        p.mB.replace(irow, irhs)(_ / diag)
      }
      for (isub <- irow + 1 to size) {
        val mult = p.mA(isub)(irow)
        for (icol <- irow to size) {
          p.mA.replace(isub, icol)(_ - p.mA(irow)(icol) * mult)
        }
        for (irhs <- 1 to nrhs) {
          p.mB.replace(isub, irhs)(_ - p.mB(irow)(irhs) * mult)
        }
      }
    }
  }

  def partialBackwardsSubstitution(elim: Int, size: Int, nrhs: Int)(implicit p: BoundElement): Unit = {
    for (irhs <- 1 to nrhs) {
      for (irow <- elim to 1 by -1) {
        p.mX.replace(irow, irhs, p.mB(irow)(irhs))
        for (icol <- irow + 1 to size) {
          p.mX.replace(irow, irhs)(_ - p.mA(irow)(icol) * p.mX(icol)(irhs))
        }
        p.mX.replace(irow, irhs)(_ / p.mA(irow)(irow))
      }
    }
  }

}
