package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.MatrixUtils
import edu.agh.kboom.MatrixUtils.{fill, sumOfIndexes, unit, weakPrecision}
import edu.agh.kboom.iga.adi.graph.problems.{LinearProblem, OneProblem}
import edu.agh.kboom.iga.adi.graph.solver._
import edu.agh.kboom.iga.adi.graph.solver.core.{Mesh, SplineSurface}

class IterativeSolverIT extends AbstractIT {

  class SolverContext(problemSize: Int) {
    implicit val mesh: Mesh = Mesh(problemSize, problemSize, problemSize, problemSize)
    val solver = StepSolver(DirectionSolver(mesh))
    val iterativeSolver = IterativeSolver(solver)
  }

  "running solver for" when {

    "element count is 12x12" should {

      "should produce valid results for f(x,y) = 1" in new SolverContext(12) {
        var lastSurface: Option[SplineSurface] = None
        iterativeSolver.solve(OneProblem, (surface, si) => {
          lastSurface = Some(surface)
          si match {
            case StepInformation(step, _) => if(step < 2) Some(OneProblem) else None
          }
        })
        weakPrecision(SplineSurface.asArray(lastSurface.get)) should equal(MatrixUtils.assembleMatrix(14)(Seq(unit)))
      }

      "should produce valid results for f(x,y) = x + y" in new SolverContext(12) {
        var lastSurface: Option[SplineSurface] = None
        iterativeSolver.solve(LinearProblem, (surface, si) => {
          lastSurface = Some(surface)
          si match {
            case StepInformation(step, _) => if(step < 2) Some(LinearProblem) else None
          }
        })
        weakPrecision(SplineSurface.asArray(lastSurface.get)) should equal(MatrixUtils.assembleMatrix(14)(Seq(fill(-1), sumOfIndexes())))
      }

    }

  }

}