package edu.agh.kboom.tree

import edu.agh.kboom.tree.Element.{COLS_BOUND_TO_NODE, ROWS_BOUND_TO_NODE}
import edu.agh.kboom.{IgaContext, Mesh, Spline}

sealed case class BoundElement(
                                v: Vertex,
                                e: Element
                              ) {
  def mA: Array[Array[Double]] = e.mA

  def mB: Array[Array[Double]] = e.mB

  def mX: Array[Array[Double]] = e.mX
}

case class Element(elements: Int) {
  val mA: Array[Array[Double]] = Array.ofDim[Double](ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)
  val mB: Array[Array[Double]] = Array.ofDim[Double](ROWS_BOUND_TO_NODE, elements)
  val mX: Array[Array[Double]] = Array.ofDim[Double](ROWS_BOUND_TO_NODE, elements)
}

object Element {

  val ROWS_BOUND_TO_NODE = 7
  val COLS_BOUND_TO_NODE = 7


  def createForX(implicit mesh: Mesh): Element = Element(mesh.xSize + IgaContext.SPLINE_ORDER + 1)

}
