package edu.agh.kboom.tree

import edu.agh.kboom.core.{Array2D, ArrayA, ArrayB, ArrayX}
import edu.agh.kboom.tree.Element.{COLS_BOUND_TO_NODE, ROWS_BOUND_TO_NODE}
import edu.agh.kboom.{IgaContext, Mesh, Spline}

sealed case class BoundElement(
                                v: Vertex,
                                e: Element
                              ) {
  def mA: ArrayA = e.mA

  def mB: ArrayB = e.mB

  def mX: ArrayX = e.mX
}

class Element(elements: Int) {
  val mA: ArrayA = Array2D.ofSize(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)
  val mB: ArrayB = Array2D.ofSize(ROWS_BOUND_TO_NODE, elements)
  val mX: ArrayX = Array2D.ofSize(ROWS_BOUND_TO_NODE, elements)
}

object Element {

  val ROWS_BOUND_TO_NODE = 7
  val COLS_BOUND_TO_NODE = 7


  def createForX(implicit mesh: Mesh): Element = Element(mesh.xSize + IgaContext.SPLINE_ORDER + 1)

  def print(e: Element): String = (0 until ROWS_BOUND_TO_NODE)
    .map(row => s"[${printRow(e.mA(row))}][${printRow(e.mX(row))}] = [${printRow(e.mB(row))}]")
    .mkString(System.lineSeparator())

  def printRow(row: Array[Double]): String = row.map(i => f"$i%+03f").mkString(",")

}
