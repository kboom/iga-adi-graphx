package edu.agh.kboom.iga.adi.graph.problems

import edu.agh.kboom.iga.adi.graph.solver.core.SplineSurface.projectedValue
import edu.agh.kboom.iga.adi.graph.solver.core.{CoefficientExtractor, IterativeProblem, Mesh}

final case class HeatTransferProblem(mesh: Mesh) extends IterativeProblem(mesh) {
  override def valueAt(c: CoefficientExtractor, x: Double, y: Double): Double = projectedValue(c, y, x)
}
