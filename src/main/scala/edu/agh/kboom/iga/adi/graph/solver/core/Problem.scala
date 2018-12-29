package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.iga.adi.graph.solver.core.Spline.{Spline1T, Spline2T, Spline3T}

abstract class Problem extends Serializable {
  /**
    * Gets the new value in (x,y) based on current projection (coefficients)
    *
    * @param c local coefficients 3x(N+2)
    * @param x
    * @param y
    * @return
    */
  def valueAt(c: (Int, Int) => Double, x: Double, y: Double): Double
}

abstract class StaticProblem extends Problem {
  final def valueAt(c: (Int, Int) => Double, x: Double, y: Double): Double =
    valueAt(x, y)

  def valueAt(x: Double, y: Double): Double
}

abstract class IterativeProblem(mesh: Mesh) extends Problem {
  def projectedValue(c: (Int, Int) => Double, x: Double, y: Double): Double = {
    val ielemx = (x / mesh.dx).toInt
    val ielemy = (y / mesh.dy).toInt
    val localx = x - mesh.dx * ielemx
    val localy = y - mesh.dy * ielemy

    Spline1T.getValue(localx) * Spline1T.getValue(localy) * c(0, 0) +
      Spline1T.getValue(localx) * Spline2T.getValue(localy) * c(0, 1) +
      Spline1T.getValue(localx) * Spline3T.getValue(localy) * c(0, 2) +
      Spline2T.getValue(localx) * Spline1T.getValue(localy) * c(1, 0) +
      Spline2T.getValue(localx) * Spline2T.getValue(localy) * c(1, 1) +
      Spline2T.getValue(localx) * Spline3T.getValue(localy) * c(1, 2) +
      Spline3T.getValue(localx) * Spline1T.getValue(localy) * c(2, 0) +
      Spline3T.getValue(localx) * Spline2T.getValue(localy) * c(2, 1) +
      Spline3T.getValue(localx) * Spline3T.getValue(localy) * c(2, 2)
  }
}