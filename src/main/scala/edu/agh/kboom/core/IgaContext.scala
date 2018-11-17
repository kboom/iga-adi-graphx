package edu.agh.kboom.core

import edu.agh.kboom.core.tree.ProblemTree

sealed case class IgaContext(mesh: Mesh, problem: (Double, Double) => Double) {
  def xTree(): ProblemTree = ProblemTree(mesh.xSize)

  def yTree(): ProblemTree = ProblemTree(mesh.ySize)
}

object IgaContext {
  val SPLINE_ORDER = 2
}
