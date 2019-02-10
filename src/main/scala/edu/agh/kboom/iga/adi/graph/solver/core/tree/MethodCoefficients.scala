package edu.agh.kboom.iga.adi.graph.solver.core.tree

import breeze.linalg.DenseMatrix
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA

object MethodCoefficients {

  def initMatrix = DenseMatrix(
    (1.0 / 20.0, 13.0 / 120, 1.0 / 120),
    (13.0 / 120.0, 45.0 / 100.0, 13.0 / 120.0),
    (1.0 / 120.0, 13.0 / 120.0, 1.0 / 20.0)
  ).t

  def bind(a: MatrixA): Unit = {
    a(0 until 3, 0 until 3) += initMatrix
  }
}
