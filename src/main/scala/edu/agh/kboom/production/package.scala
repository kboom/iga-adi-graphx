package edu.agh.kboom

import edu.agh.kboom.core.Array2D
import edu.agh.kboom.tree.BoundElement

package object production {

  trait Production[MSG] {

    def prepare(src: BoundElement)(implicit ctx: IgaTaskContext): Unit = {

    }

    def send(src: BoundElement)(implicit ctx: IgaTaskContext): Seq[MSG]

    def merge(a: MSG, b: MSG)(implicit ctx: IgaTaskContext): MSG = {
      throw new IllegalStateException()
    }

    def receive(dst: BoundElement, msg: MSG)(implicit ctx: IgaTaskContext): Unit

  }

  abstract class ProductionMessage() {
    val production: Production[Any]
  }

  sealed case class ComponentA(v: Array2D)

  sealed case class ComponentB(v: Array2D)

  /*
  protected void swapDofs(int a, int b, int size, int nrhs) {
        for (int i = 1; i <= size; i++) {
            double temp = node.m_a[a][i];
            node.m_a[a][i] = node.m_a[b][i];
            node.m_a[b][i] = temp;
        }
        for (int i = 1; i <= size; i++) {
            double temp = node.m_a[i][a];
            node.m_a[i][a] = node.m_a[i][b];
            node.m_a[i][b] = temp;
        }
        for (int i = 1; i <= nrhs; i++) {
            double temp = node.m_b[a][i];
            node.m_b[a][i] = node.m_b[b][i];
            node.m_b[b][i] = temp;

            temp = node.m_x[a][i];
            node.m_x[a][i] = node.m_x[b][i];
            node.m_x[b][i] = temp;
        }
    }
   */

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


  /*
  Vertex partial_forward_elimination(Vertex T, int elim, int size, int nrhs) {
        for (int irow = 1; irow <= elim; irow++) {
            double diag = T.m_a[irow][irow];
            for (int icol = irow; icol <= size; icol++) {
                T.m_a[irow][icol] /= diag;
            }
            for (int irhs = 1; irhs <= nrhs; irhs++) {
                T.m_b[irow][irhs] /= diag;
            }
            for (int isub = irow + 1; isub <= size; isub++) {
                double mult = T.m_a[isub][irow];
                for (int icol = irow; icol <= size; icol++) {
                    T.m_a[isub][icol] -= T.m_a[irow][icol] * mult;
                }
                for (int irhs = 1; irhs <= nrhs; irhs++) {
                    T.m_b[isub][irhs] -= T.m_b[irow][irhs] * mult;
                }
            }
        }
        return T;
    }

    Vertex partial_backward_substitution(Vertex T, int elim, int size, int nrhs) {
        for (int irhs = 1; irhs <= nrhs; irhs++) {
            for (int irow = elim; irow >= 1; irow--) {
                T.m_x[irow][irhs] = T.m_b[irow][irhs];
                for (int icol = irow + 1; icol <= size; icol++) {
                    T.m_x[irow][irhs] -= T.m_a[irow][icol] * T.m_x[icol][irhs];
                }
                T.m_x[irow][irhs] /= T.m_a[irow][irow];
            }
        }
        return T;
    }
   */

}
