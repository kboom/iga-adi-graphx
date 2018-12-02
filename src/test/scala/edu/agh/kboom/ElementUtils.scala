package edu.agh.kboom

import edu.agh.kboom.MatrixUtils.{dummyAMatrix, dummyBMatrix, dummyXMatrix, identityMatrix, indexedMatrix}
import edu.agh.kboom.core.tree.Element.ROWS_BOUND_TO_NODE
import edu.agh.kboom.core.tree.{BoundElement, Element, Vertex}
import edu.agh.kboom.core.{MatrixA, MatrixB, MatrixX, Mesh}

object ElementUtils {

  def elementBoundTo(mesh: Mesh, vertex: Vertex)(
    mA: Array[Array[Double]] = identityMatrix(mesh.xSize),
    mB: Array[Array[Double]] = indexedMatrix(ROWS_BOUND_TO_NODE, Element.elementCount(mesh)),
    mX: Array[Array[Double]] = indexedMatrix(ROWS_BOUND_TO_NODE, Element.elementCount(mesh))
  ): BoundElement = {
    val element = Element.createForX(mesh)
    element.mA.add(MatrixA(mA))
    element.mB.add(MatrixB(mB))
    element.mX.add(MatrixX(mX))
    BoundElement(vertex, element)
  }

  def dummyBoundElement(vertex: Vertex, seed: Int = 1)(implicit mesh: Mesh): BoundElement = {
    val element = Element.createForX(mesh)
    element.mA.add(dummyAMatrix(seed))
    element.mB.add(dummyBMatrix(seed))
    element.mX.add(dummyXMatrix(seed))
    BoundElement(vertex, element)
  }

  def dummyElement(seed: Int = 1)(implicit mesh: Mesh): Element = {
    val element = Element.createForX(mesh)
    element.mA.add(dummyAMatrix(seed))
    element.mB.add(dummyBMatrix(seed))
    element.mX.add(dummyXMatrix(seed))
    element
  }

}
