package edu.agh.kboom

import edu.agh.kboom.MatrixUtils.{dummyAMatrix, dummyBMatrix, dummyXMatrix}
import edu.agh.kboom.core.Mesh
import edu.agh.kboom.core.tree.{BoundElement, Element, Vertex}

object ElementUtils {

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
