package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.iga.adi.graph.solver.core.MatrixUtil.DenseMatrixUtil
import edu.agh.kboom.iga.adi.graph.solver.core.tree.IgaElement

package object production {

  def swapDofs(a: Int, b: Int, size: Int)(implicit p: IgaElement): Unit = {
    p.mA.swapRows(a, b)
    p.mA.swapCols(a, b)
    p.mB.swapRows(a, b)
    p.mX.swapRows(a, b)
  }

  def partialForwardElimination(elim: Int, size: Int)(implicit p: IgaElement): Unit = {
    for (irow <- 0 until elim) {
      val diag = p.mA(irow, irow)
      p.mA(irow, irow until size) :/= diag
      p.mB(irow, irow until size) :/= diag

      for (isub <- irow + 1 until size) {
        val mult = p.mA(isub, irow)

        p.mA(isub, irow until size) :-= p.mA(irow, irow until size) * mult
        p.mB(isub, ::) :-= p.mB(irow, ::) * mult
      }
    }
  }

  /**
    * Try to avoid loops - try to do reductions and so on
    */
  def partialBackwardsSubstitution(elim: Int, size: Int)(implicit p: IgaElement): Unit = {
    for (irhs <- 0 until p.dofs) {
      for (irow <- (elim - 1) to 0 by -1) {
        p.mX(irow, irhs to irhs) := p.mB(irow, irhs)

        for (icol <- irow + 1 until size) {
          p.mX(irow, irhs to irhs) :+= -p.mA(irow, icol) * p.mX(icol, irhs)
        }
        p.mX(irow, irhs to irhs) :/= p.mA(irow, irow)
      }
    }
  }

}
