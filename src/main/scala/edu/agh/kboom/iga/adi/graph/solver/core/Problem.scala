package edu.agh.kboom.iga.adi.graph.solver.core

abstract class Problem extends Serializable {
  def valueAt(x: Double, y: Double): Double
}

case class IterativeProblem(solution: Solution) extends Problem {
  override def valueAt(x: Double, y: Double): Double = solution.getValue(x, y)
}

object LinearProblem extends Problem {
  override def valueAt(x: Double, y: Double): Double = x + y
}

object OneProblem extends Problem {
  override def valueAt(x: Double, y: Double): Double = 1
}
