package edu.agh.kboom

import edu.agh.kboom.MatrixUtils._
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

  def featuredBoundElement(vertex: Vertex, feature: Int)(implicit mesh: Mesh): BoundElement = {
    val element = Element.createForX(mesh)
    element.mA.add(dummyAMatrix(feature))
    element.mB.add(dummyBMatrix(feature))
    element.mX.add(dummyXMatrix(feature))
    BoundElement(vertex, element)
  }

  def constBoundElement(vertex: Vertex, seed: Int = 0)(implicit mesh: Mesh): BoundElement = {
    val element = Element.createForX(mesh)
    element.mA.add(constAMatrix(seed))
    element.mB.add(constBMatrix(seed))
    element.mX.add(constXMatrix(seed))
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
