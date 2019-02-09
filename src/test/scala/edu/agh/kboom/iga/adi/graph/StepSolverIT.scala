package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.MatrixUtils
import edu.agh.kboom.MatrixUtils.{fill, sumOfIndexes, unit, weakPrecision}
import edu.agh.kboom.iga.adi.graph.problems.{LinearProblem, OneProblem}
import edu.agh.kboom.iga.adi.graph.solver.core.{Mesh, SplineSurface}
import edu.agh.kboom.iga.adi.graph.solver.{DirectionSolver, IgaContext, ProjectionLoader, StepSolver}

import scala.language.postfixOps

class StepSolverIT extends AbstractIT {

  class SolverContext(problemSize: Int) {
    implicit val mesh: Mesh = Mesh(problemSize, problemSize, problemSize, problemSize)
    val solver = StepSolver(DirectionSolver(mesh))
  }

  "running solver for" when {

    "element count is 12x12" should {

      "should produce valid results for f(x,y) = 1" in new SolverContext(12) {
        val solution = solver.solve(IgaContext(mesh, OneProblem))(ProjectionLoader.loadSurface(mesh, OneProblem))
        weakPrecision(SplineSurface.asArray(solution)) should equal(MatrixUtils.assembleMatrix(14)(Seq(unit)))
      }

      "should produce valid results for f(x,y) = x + y" in new SolverContext(12) {
        val solution = solver.solve(IgaContext(mesh, LinearProblem))(ProjectionLoader.loadSurface(mesh, LinearProblem))
        weakPrecision(SplineSurface.asArray(solution)) should equal(MatrixUtils.assembleMatrix(14)(Seq(fill(-1), sumOfIndexes())))
      }

    }

    "element count is 24x24" should {

      "should produce valid results" in new SolverContext(24) {
        val solution = solver.solve(IgaContext(mesh, OneProblem))(ProjectionLoader.loadSurface(mesh, OneProblem))
        weakPrecision(SplineSurface.asArray(solution)) should equal(MatrixUtils.assembleMatrix(26)(Seq(unit)))
      }

      "should produce valid results for f(x,y) = x + y" in new SolverContext(24) {
        val solution = solver.solve(IgaContext(mesh, LinearProblem))(ProjectionLoader.loadSurface(mesh, LinearProblem))
        weakPrecision(SplineSurface.asArray(solution)) should equal(MatrixUtils.assembleMatrix(26)(Seq(fill(-1), sumOfIndexes())))
      }

    }

  }

}
