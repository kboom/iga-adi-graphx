package edu.agh.kboom.iga.adi.graph.problems

import edu.agh.kboom.iga.adi.graph.solver.core.StaticProblem

case class FunctionProblem(f: (Double, Double) => Double) extends StaticProblem {
  override def valueAt(x: Double, y: Double): Double = f(x, y)
}

case class RadialProblem(r: Int, cx: Double, cy: Double, vi: Double = 1, vo: Double = 0) extends StaticProblem {

  private def withinCircle(x: Double, y: Double): Boolean = {
    Math.pow(cx - x, 2) + Math.pow(cy - y, 2) <= Math.pow(r, 2)
  }

  override def valueAt(x: Double, y: Double): Double = if (withinCircle(x, y)) vi else vo
}

object LinearProblem extends StaticProblem {
  override def valueAt(x: Double, y: Double): Double = x + y
}

object OneProblem extends StaticProblem {
  override def valueAt(x: Double, y: Double): Double = 1
}

