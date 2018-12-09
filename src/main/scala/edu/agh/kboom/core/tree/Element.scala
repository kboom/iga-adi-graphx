package edu.agh.kboom.core.tree

import edu.agh.kboom.core._
import edu.agh.kboom.core.tree.Element.{COLS_BOUND_TO_NODE, ROWS_BOUND_TO_NODE}

sealed case class IgaElement(v: Vertex, e: Element, p: Int = 0) {
  def mA: MatrixA = e.mA
  def mB: MatrixB = e.mB
  def mX: MatrixX = e.mX

  def hasMorePressureThan(o: IgaElement): Boolean = p > o.p

  def withIncreasedPressure(): IgaElement = IgaElement(v, e, p + 1)
}

case class Element(elements: Int) {
  val mA: MatrixA = MatrixA.ofDim(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)
  val mB: MatrixB = MatrixB.ofDim(ROWS_BOUND_TO_NODE, elements)
  val mX: MatrixX = MatrixX.ofDim(ROWS_BOUND_TO_NODE, elements)
}

object IgaElement {

  def print(e: IgaElement): String = f"Element pressure=${e.p}\n${Element.print(e.e)}"

  def copy(src: IgaElement): IgaElement = IgaElement(src.v, src.e, src.p)

}

object Element {
  val ROWS_BOUND_TO_NODE = 6
  val COLS_BOUND_TO_NODE = 6

  def elementCount(implicit mesh: Mesh) : Int = mesh.xDofs

  def createForX(implicit mesh: Mesh): Element = Element(elementCount)

  def print(e: Element): String = (0 until ROWS_BOUND_TO_NODE)
    .map(row => s"[${printRow(e.mA.row(row))}][${printRow(e.mX.row(row))}] = [${printRow(e.mB.row(row))}]")
    .mkString(System.lineSeparator())

  def printRow(row: Array[Double]): String = row.map(i => f"$i%+03f").mkString(",")

}
