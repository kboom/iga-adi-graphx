package edu.agh.kboom.iga.adi.graph.core

abstract class Problem extends Serializable {
  def valueAt(x: Double, y: Double): Double
}

object LinearProblem extends Problem {
  override def valueAt(x: Double, y: Double): Double = x + y
}

object OneProblem extends Problem {
  override def valueAt(x: Double, y: Double): Double = 1
}
