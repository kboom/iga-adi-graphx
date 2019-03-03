package edu.agh.kboom

import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{Element, IgaElement, Vertex}

object ElementUtils {

  def cleanElementBoundTo(mesh: Mesh, vertex: Vertex): IgaElement = {
    val element = Element.createForX(mesh)
    element.mA :+= generatedMatrixA(Seq(fill(0)))
    element.mB :+= generatedMatrixB(Seq(fill(0)))(mesh)
    element.mX :+= generatedMatrixX(Seq(fill(0)))(mesh)
    IgaElement(vertex, element)
  }

  def elementBoundTo(mesh: Mesh, vertex: Vertex)(
    mA: MatrixA = generatedMatrixA(Seq(identity)),
    mB: MatrixB = generatedMatrixB(Seq(index))(mesh),
    mX: MatrixX = generatedMatrixX(Seq(index))(mesh)
  ): IgaElement = {
    val element = Element.createForX(mesh)
    element.mA += mA
    element.mB += mB
    element.mX += mX
    IgaElement(vertex, element)
  }

  def element(mesh: Mesh)(
    mA: MatrixA = generatedMatrixA(Seq(identity)),
    mB: MatrixB = generatedMatrixB(Seq(index))(mesh),
    mX: MatrixX = generatedMatrixX(Seq(index))(mesh)
  ): Element = {
    val element = Element.createForX(mesh)
    element.mA += mA
    element.mB += mB
    element.mX += mX
    element
  }

  def weakPrecision(e: IgaElement): IgaElement = IgaElement(e.v, weakPrecision(e.e), e.p)

  def weakPrecision(e: Element): Element = {
    val ne = Element.createForX(e.elements)
    ne.mA += MatrixUtils.weakPrecision(e.mA)
    ne.mB += MatrixUtils.weakPrecision(e.mB)
    ne.mX += MatrixUtils.weakPrecision(e.mX)
    ne
  }

}
