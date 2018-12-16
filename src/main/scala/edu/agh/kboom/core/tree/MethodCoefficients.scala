package edu.agh.kboom.core.tree

import edu.agh.kboom.core.MatrixA

object MethodCoefficients {

  def bind(a: MatrixA): Unit = {
    a.replace(0, 0, 1.0 / 20.0)
    a.replace(0, 1, 13.0 / 120)
    a.replace(0, 2, 1.0 / 120)
    a.replace(1, 0, 13.0 / 120.0)
    a.replace(1, 1, 45.0 / 100.0)
    a.replace(1, 2, 13.0 / 120.0)
    a.replace(2, 0, 1.0 / 120.0)
    a.replace(2, 1, 13.0 / 120.0)
    a.replace(2, 2, 1.0 / 20.0)
  }
}
