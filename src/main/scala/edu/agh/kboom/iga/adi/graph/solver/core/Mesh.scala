package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree

case class Mesh(xSize: Int, ySize: Int, xRes: Int, yRes: Int) {
  val xDofs: Int = xSize + Mesh.SPLINE_ORDER
  val yDofs: Int = ySize + Mesh.SPLINE_ORDER
  val dx: Int = xRes / xSize
  val dy: Int = yRes / ySize

  def totalNodes(): Int = {
    implicit val tree: ProblemTree = ProblemTree(xSize)
    val leafHeight = ProblemTree.branchingHeight
    Math.pow(2, leafHeight).toInt + ProblemTree.strengthOfLeaves().toInt - 1
  }
}

object Mesh {

  val SPLINE_ORDER = 2

}
