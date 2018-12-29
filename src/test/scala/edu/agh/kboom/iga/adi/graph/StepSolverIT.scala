package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.MatrixUtils
import edu.agh.kboom.MatrixUtils.{fill, sumOfIndexes, unit, weakPrecision}
import edu.agh.kboom.iga.adi.graph.solver.core.{Mesh, Solution}
import edu.agh.kboom.iga.adi.graph.solver.{DirectionSolver, IgaContext, StepSolver}

class StepSolverIT extends AbstractIT {

  class SolverContext(problemSize: Int) {
    implicit val mesh: Mesh = Mesh(problemSize, problemSize, problemSize, problemSize)
    val solver = StepSolver(DirectionSolver(mesh))
  }

  "running solver for" when {

    "element count is 12x12" should {

      "should produce valid results for f(x,y) = 1" in new SolverContext(12) {
        val solution = solver.solve(IgaContext(mesh, (_, _) => 1))
        weakPrecision(Solution.asArray(solution)) should contain theSameElementsAs (MatrixUtils.assembleMatrix(14)(Seq(unit)))
      }

      "should produce valid results for f(x,y) = x + y" in new SolverContext(12) {
        val solution = solver.solve(IgaContext(mesh, (x, y) => x + y))
        weakPrecision(Solution.asArray(solution)) should contain theSameElementsAs (MatrixUtils.assembleMatrix(14)(Seq(fill(-1), sumOfIndexes())))
      }

    }

    "element count is 24x24" should {

      "should produce valid results" in new SolverContext(24) {
        val solution = solver.solve(IgaContext(mesh, (_, _) => 1))
        weakPrecision(Solution.asArray(solution)) should contain theSameElementsAs (MatrixUtils.assembleMatrix(26)(Seq(unit)))
      }

      "should produce valid results for f(x,y) = x + y" in new SolverContext(24) {
        val solution = solver.solve(IgaContext(mesh, (x, y) => x + y))
        weakPrecision(Solution.asArray(solution)) should contain theSameElementsAs (MatrixUtils.assembleMatrix(26)(Seq(fill(-1), sumOfIndexes())))
      }

    }

  }

}
