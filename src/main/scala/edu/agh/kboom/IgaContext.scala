package edu.agh.kboom

import edu.agh.kboom.tree.ProblemTree

sealed case class IgaContext(mesh: Mesh, problem: (Double, Double) => Double) {

  def xTree(): ProblemTree = ProblemTree(mesh.xSize)

  def yTree(): ProblemTree = ProblemTree(mesh.ySize)

}

object IgaContext {
  val SPLINE_ORDER = 2
}
