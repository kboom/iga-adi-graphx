package edu.agh.kboom.iga.adi.graph.problems

import edu.agh.kboom.iga.adi.graph.solver.core.{IterativeProblem, Mesh, Projection}

final case class HeatTransferProblem(mesh: Mesh) extends IterativeProblem(mesh) {
  override def valueAt(c: (Int, Int) => Double, x: Double, y: Double): Double = Projection.projectedValue(c, x, y)
}
