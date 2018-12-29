package edu.agh.kboom.iga.adi.graph.problems

import edu.agh.kboom.iga.adi.graph.solver.core.{IterativeProblem, Mesh}

final case class HeatTransferProblem(mesh: Mesh) extends IterativeProblem(mesh) {
  override def valueAt(c: (Int, Int) => Double, x: Double, y: Double): Double = projectedValue(c, x, y)
}
