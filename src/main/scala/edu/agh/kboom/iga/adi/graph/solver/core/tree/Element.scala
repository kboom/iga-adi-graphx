package edu.agh.kboom.iga.adi.graph.solver.core.tree

import breeze.linalg.DenseVector
import edu.agh.kboom.iga.adi.graph.solver.LoggingConfig
import edu.agh.kboom.iga.adi.graph.solver.SolverConfig.LoadedSolverConfig
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core._

sealed case class IgaElement(v: Vertex, e: Element, p: Int = 0) {
  def mA: MatrixA = e.mA

  def mB: MatrixB = e.mB

  def mX: MatrixX = e.mX

  def hasMorePressureThan(o: IgaElement): Boolean = p > o.p

  def withIncreasedPressure(): IgaElement = IgaElement(v, e, p + 1)

  def swapElement(se: Element): IgaElement = IgaElement(v, se, p)

  def dofs = mX.cols

//  override def hashCode(): Int = v.hashCode() // by doing this we ensure that vertices "wont fly around"
}

case class Element(elements: Int, mA: MatrixA, mB: MatrixB, mX: MatrixX)

object IgaElement {

  val LoggingConfig: LoggingConfig = LoadedSolverConfig.logging

  def print(e: IgaElement): String = f"Element pressure=${e.p}\n${Element.print(e.e)}"

  def copy(src: IgaElement): IgaElement = IgaElement(src.v, Element.copy(src.e), src.p)

  def reflect(src: IgaElement): IgaElement = IgaElement(src.v, src.e, src.p)

}

object Element {
  val ROWS_BOUND_TO_NODE = 6
  val COLS_BOUND_TO_NODE = 6

  def elementCount(implicit mesh: Mesh): Int = mesh.xDofs

  def createForX(implicit mesh: Mesh): Element = createForX(elementCount)

  def createForX(elements: Int): Element = Element(
    elements,
    mA = createMatA(),
    mB = createMatB(elements),
    mX = createMatX(elements)
  )

  def createMatA(): MatrixA = MatrixA.ofDim(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)
  def createMatB(elements: Int): MatrixB = MatrixA.ofDim(ROWS_BOUND_TO_NODE, elements)
  def createMatB(implicit mesh: Mesh): MatrixB = createMatB(elementCount)

  def createMatX(elements: Int): MatrixX = MatrixX.ofDim(ROWS_BOUND_TO_NODE, elements)
  def createMatX(implicit mesh: Mesh): MatrixX = createMatX(elementCount)

  def print(e: Element): String = (0 until ROWS_BOUND_TO_NODE)
    .map(row => s"${printRow(e.mA(row, ::).inner)}\t|\t${printRow(e.mX(row, ::).inner)}\t|\t${printRow(e.mB(row, ::).inner)}")
    .mkString(System.lineSeparator())

  def printRow(row: DenseVector[Double]): String = row.map(i => f"$i%+.3f").data.mkString(" ")

  def copy(src: Element): Element = {
    val dst = createForX(src.elements)
    dst.mX :+= src.mX
    dst.mA :+= src.mA
    dst.mB :+= src.mB
    dst
  }

}
