package edu.agh.kboom

abstract class Problem {
  def valueAt(x: Double, y: Double): Double
}

sealed class OneProblem extends Problem {
  override def valueAt(x: Double, y: Double): Double = 1
}
