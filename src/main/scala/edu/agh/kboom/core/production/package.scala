package edu.agh.kboom.core

import edu.agh.kboom.core.tree.IgaElement

package object production {

  def swapDofs(a: Int, b: Int, size: Int, nrhs: Int)(implicit p: IgaElement): Unit = {
    for (i <- 0 until size) {
      p.mA.swap(a, i, b, i)
    }
    for (i <- 0 until size) {
      p.mA.swap(i, a, i, b)
    }
    for (i <- 0 until nrhs) {
      p.mB.swap(a, i, b, i)
      p.mX.swap(a, i, b, i)
    }
  }

  def partialForwardElimination(elim: Int, size: Int, nrhs: Int)(implicit p: IgaElement): Unit = {
    for (irow <- 0 until elim) {
      val diag = p.mA(irow)(irow)
      for (icol <- irow until size) {
        p.mA.mapEntry(irow, icol)(_ / diag)
      }
      for (irhs <- 0 until nrhs) {
        p.mB.mapEntry(irow, irhs)(_ / diag)
      }
      for (isub <- irow + 1 until size) {
        val mult = p.mA(isub)(irow)
        for (icol <- irow until size) {
          p.mA.mapEntry(isub, icol)(_ - p.mA(irow)(icol) * mult)
        }
        for (irhs <- 0 until nrhs) {
          p.mB.mapEntry(isub, irhs)(_ - p.mB(irow)(irhs) * mult)
        }
      }
    }
  }

  def partialBackwardsSubstitution(elim: Int, size: Int, nrhs: Int)(implicit p: IgaElement): Unit = {
    for (irhs <- 0 until nrhs) {
      for (irow <- (elim - 1) to 0 by -1) {
        p.mX.replace(irow, irhs, p.mB(irow)(irhs))
        for (icol <- irow + 1 until size) {
          p.mX.mapEntry(irow, irhs)(_ - p.mA(irow)(icol) * p.mX(icol)(irhs))
        }
        p.mX.mapEntry(irow, irhs)(_ / p.mA(irow)(irow))
      }
    }
  }

}
