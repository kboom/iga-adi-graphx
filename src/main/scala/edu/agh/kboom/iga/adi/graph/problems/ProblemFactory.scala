package edu.agh.kboom.iga.adi.graph.problems

import edu.agh.kboom.iga.adi.graph.solver.core.StaticProblem
import edu.agh.kboom.iga.adi.graph.solver.{HeatTransferProblemConfig, ProblemConfig, ProjectionProblemConfig}

object ProblemFactory {

  def initialProblem(cfg: ProblemConfig): StaticProblem = cfg match {
    case HeatTransferProblemConfig(size, _) => RadialProblem(3, size / 2, size / 2)
    case ProjectionProblemConfig(_, _) => LinearProblem
    case _ => throw new IllegalStateException(f"Unknown problem type ($cfg)")
  }

}
