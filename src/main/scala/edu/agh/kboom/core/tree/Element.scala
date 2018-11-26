package edu.agh.kboom.core.tree

import edu.agh.kboom.core.tree.Element.{COLS_BOUND_TO_NODE, ROWS_BOUND_TO_NODE}
import edu.agh.kboom.core._

sealed case class BoundElement(v: Vertex, e: Element) {
  def mA: MatrixA = e.mA
  def mB: MatrixB = e.mB
  def mX: MatrixX = e.mX
}

case class Element(elements: Int) {
  val mA: MatrixA = MatrixA.ofDim(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)
  val mB: MatrixB = MatrixB.ofDim(ROWS_BOUND_TO_NODE, elements)
  val mX: MatrixX = MatrixX.ofDim(ROWS_BOUND_TO_NODE, elements)
}

object Element {
  val ROWS_BOUND_TO_NODE = 6
  val COLS_BOUND_TO_NODE = 6

  def createForX(implicit mesh: Mesh): Element = Element(mesh.xSize + IgaContext.SPLINE_ORDER + 1)

  def print(e: Element): String = (0 until ROWS_BOUND_TO_NODE)
    .map(row => s"[${printRow(e.mA.row(row))}][${printRow(e.mX.row(row))}] = [${printRow(e.mB.row(row))}]")
    .mkString(System.lineSeparator())

  def printRow(row: Array[Double]): String = row.map(i => f"$i%+03f").mkString(",")

}
