package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree

sealed trait SolverDirection

case object HORIZONTAL extends SolverDirection

case object VERTICAL extends SolverDirection

sealed case class IgaContext(mesh: Mesh, problem: (Double, Double) => Double, direction: SolverDirection = HORIZONTAL) {
  def changedDirection(): IgaContext = IgaContext(mesh, problem, VERTICAL)

  def xTree(): ProblemTree = ProblemTree(mesh.xSize)

  def yTree(): ProblemTree = ProblemTree(mesh.ySize)

  def tree(): ProblemTree = direction match {
    case HORIZONTAL => xTree()
    case VERTICAL => yTree()
    case _ => throw new IllegalStateException("Unknown direction")
  }
}

object IgaContext {
  val SPLINE_ORDER = 2
}
