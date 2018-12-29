package edu.agh.kboom.iga.adi.graph.problems

import edu.agh.kboom.iga.adi.graph.solver.core.StaticProblem

object LinearProblem extends StaticProblem {
  override def valueAt(x: Double, y: Double): Double = x + y
}

object OneProblem extends StaticProblem {
  override def valueAt(x: Double, y: Double): Double = 1
}

