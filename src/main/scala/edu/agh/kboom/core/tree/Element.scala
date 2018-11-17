package edu.agh.kboom.core.tree

import edu.agh.kboom.core.tree.Element.{COLS_BOUND_TO_NODE, ROWS_BOUND_TO_NODE}
import edu.agh.kboom.core._

sealed case class BoundElement(v: Vertex, e: Element) {
  def mA: ArrayA = e.mA
  def mB: ArrayB = e.mB
  def mX: ArrayX = e.mX
}

case class Element(elements: Int) {
  val mA: ArrayA = ArrayA.ofDim(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)
  val mB: ArrayB = ArrayB.ofDim(ROWS_BOUND_TO_NODE, elements)
  val mX: ArrayX = ArrayX.ofDim(ROWS_BOUND_TO_NODE, elements)
}

object Element {
  val ROWS_BOUND_TO_NODE = 7
  val COLS_BOUND_TO_NODE = 7

  def createForX(implicit mesh: Mesh): Element = Element(mesh.xSize + IgaContext.SPLINE_ORDER + 1)

  def print(e: Element): String = (0 until ROWS_BOUND_TO_NODE)
    .map(row => s"[${printRow(e.mA.row(row))}][${printRow(e.mX.row(row))}] = [${printRow(e.mB.row(row))}]")
    .mkString(System.lineSeparator())

  def printRow(row: Array[Double]): String = row.map(i => f"$i%+03f").mkString(",")

}
