package edu.agh.kboom.core

abstract class Problem extends Serializable {
  def valueAt(x: Double, y: Double): Double
}

object LinearProblem extends Problem {
  override def valueAt(x: Double, y: Double): Double = x
}

object OneProblem extends Problem {
  override def valueAt(x: Double, y: Double): Double = 1
}
