package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.MatrixUtils
import edu.agh.kboom.MatrixUtils.{unit, weakPrecision}
import edu.agh.kboom.iga.adi.graph.problems.{HeatTransferProblem, OneProblem, RadialProblem}
import edu.agh.kboom.iga.adi.graph.solver.core.{Mesh, Problem, SplineSurface, StaticProblem}
import edu.agh.kboom.iga.adi.graph.solver.{DirectionSolver, IterativeSolver, StepInformation, StepSolver}

class HeatTransferIT extends AbstractIT {

  class SolverContext(problemSize: Int) {
    implicit val mesh: Mesh = Mesh(problemSize, problemSize, problemSize, problemSize)
    val solver = StepSolver(DirectionSolver(mesh))
    val iterativeSolver = IterativeSolver(solver)
  }

  private def runSolver(solver: IterativeSolver)(p: StaticProblem, np: Problem): SplineSurface = {
    var lastSurface: Option[SplineSurface] = None
    solver.solve(p, (surface, si) => {
      lastSurface = Some(surface)
      si match {
        case StepInformation(step, _) => if (step < 2) Some(np) else None
      }
    })
    lastSurface.get
  }

  "running heat transfer for" when {

    "steady state surface" should {

      "should not modify the surface" in new SolverContext(12) {
        val finalSurface = runSolver(iterativeSolver)(OneProblem, HeatTransferProblem(mesh))
        weakPrecision(SplineSurface.asArray(finalSurface)) should equal(MatrixUtils.assembleMatrix(14)(Seq(unit)))
      }

    }

  }

}