package edu.agh.kboom

import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom.core.tree.{BoundElement, Element, Vertex}
import edu.agh.kboom.core.{MatrixA, MatrixB, MatrixX, Mesh}

object ElementUtils {

  def elementBoundTo(mesh: Mesh, vertex: Vertex)(
    mA: MatrixA = generatedMatrixA(Seq(identity)),
    mB: MatrixB = generatedMatrixB(Seq(index))(mesh),
    mX: MatrixX = generatedMatrixX(Seq(index))(mesh)
  ): BoundElement = {
    val element = Element.createForX(mesh)
    element.mA.add(mA)
    element.mB.add(mB)
    element.mX.add(mX)
    BoundElement(vertex, element)
  }

}
