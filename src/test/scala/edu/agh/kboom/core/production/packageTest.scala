package edu.agh.kboom.core.production

import edu.agh.kboom.MatrixUtils.{dummyAMatrix, dummyBMatrix, fromVector}
import edu.agh.kboom.UnitSpec
import edu.agh.kboom.core.tree.{BoundElement, Element, RootVertex}
import edu.agh.kboom.core.{MatrixA, Mesh}


class packageTest extends UnitSpec {

  implicit val mesh: Mesh = Mesh(12, 12, 12, 12)
  implicit val element: Element = Element.createForX(mesh)
  implicit val boundElement: BoundElement = BoundElement(RootVertex(), element)

  "partialForwardElimination" should "eliminate 1 in 5x5 matrix" in {
    element.mA.add(dummyAMatrix())
    element.mB.add(dummyBMatrix())

    partialForwardElimination(1, 6, mesh.yDofs)

    boundElement.mA shouldEqual MatrixA(fromVector(2, 2)(
      1, 0,
      0, 1
    ))
  }

}
