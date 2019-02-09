package edu.agh.kboom.iga.adi.graph.problems

import edu.agh.kboom.iga.adi.graph.solver.core.StaticProblem
import edu.agh.kboom.iga.adi.graph.solver.{HeatTransferProblemConfig, OneProjectionProblemConfig, ProblemConfig, ProjectionProblemConfig}

object ProblemFactory {

  def initialProblem(cfg: ProblemConfig): StaticProblem = cfg match {
    case HeatTransferProblemConfig(size, _) => RadialProblem(3, size / 2, size / 2)
    case ProjectionProblemConfig(_, _) => HorizontalProblem
    case OneProjectionProblemConfig(_, _) => OneProblem
    case _ => throw new IllegalStateException(f"Unknown problem type ($cfg)")
  }

}
