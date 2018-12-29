package edu.agh.kboom

import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{Element, IgaElement, Vertex}
import edu.agh.kboom.iga.adi.graph.solver.core._

object ElementUtils {

  def cleanElementBoundTo(mesh: Mesh, vertex: Vertex)(
    mA: MatrixA = generatedMatrixA(Seq(fill(0))),
    mB: MatrixB = generatedMatrixB(Seq(fill(0)))(mesh),
    mX: MatrixX = generatedMatrixX(Seq(fill(0)))(mesh)
  ): IgaElement = {
    val element = Element.createForX(mesh)
    element.mA.add(mA)
    element.mB.add(mB)
    element.mX.add(mX)
    IgaElement(vertex, element)
  }

  def elementBoundTo(mesh: Mesh, vertex: Vertex)(
    mA: MatrixA = generatedMatrixA(Seq(identity)),
    mB: MatrixB = generatedMatrixB(Seq(index))(mesh),
    mX: MatrixX = generatedMatrixX(Seq(index))(mesh)
  ): IgaElement = {
    val element = Element.createForX(mesh)
    element.mA.add(mA)
    element.mB.add(mB)
    element.mX.add(mX)
    IgaElement(vertex, element)
  }

  def weakPrecision(e: IgaElement): IgaElement = IgaElement(e.v, weakPrecision(e.e), e.p)

  def weakPrecision(e: Element): Element = {
    val ne = Element(e.elements)
    ne.mA.add(MatrixUtils.weakPrecision(e.mA))
    ne.mB.add(MatrixUtils.weakPrecision(e.mB))
    ne.mX.add(MatrixUtils.weakPrecision(e.mX))
    ne
  }

}
