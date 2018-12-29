package edu.agh.kboom.iga.adi.graph.solver.core

abstract class Problem extends Serializable {
  /**
    * Gets the new value in (x,y) based on current projection (coefficients)
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

case class IterativeProblem(solution: Projection) extends Problem {
  override def valueAt(c: (Int, Int) => Double, x: Double, y: Double): Double = solution.getValue(x, y)
}

object LinearProblem extends StaticProblem {
  override def valueAt(x: Double, y: Double): Double = x + y
}

object OneProblem extends StaticProblem {
  override def valueAt(x: Double, y: Double): Double = 1
}
