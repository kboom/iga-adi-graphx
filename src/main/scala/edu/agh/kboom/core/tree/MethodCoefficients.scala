package edu.agh.kboom.core.tree

import edu.agh.kboom.core.MatrixA

object MethodCoefficients {

  def bind(a: MatrixA): Unit = {
    a.replace(1, 1, 1.0 / 20.0 + 1.0 / 3.0)
    a.replace(1, 2, 13.0 / 120 - 1.0 / 6.0)
    a.replace(1, 3, 1.0 / 120 - 1.0 / 6.0)
    a.replace(2, 1, 13.0 / 120.0 - 1.0 / 6.0)
    a.replace(2, 2, 45.0 / 100.0 + 1.0 / 3.0)
    a.replace(2, 3, 13.0 / 120.0 - 1.0 / 6.0)
    a.replace(3, 1, 1.0 / 120.0 - 1.0 / 6.0)
    a.replace(3, 2, 13.0 / 120.0 - 1.0 / 6.0)
    a.replace(3, 3, 1.0 / 20.0 + 1.0 / 3.0)
  }

}
