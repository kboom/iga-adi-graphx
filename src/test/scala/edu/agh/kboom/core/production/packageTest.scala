package edu.agh.kboom.core.production

import edu.agh.kboom.{MatrixUtils, UnitSpec}
import edu.agh.kboom.core.{ArrayA, Mesh}
import edu.agh.kboom.core.tree.{BoundElement, Element, RootVertex}


class packageTest extends UnitSpec {

  implicit val mesh: Mesh = Mesh(12, 12, 12, 12)
  implicit val element: BoundElement = BoundElement(RootVertex(), Element.createForX(mesh))

  "partialForwardElimination" should "eliminate 1 in 5x5 matrix" in {
    partialForwardElimination(1, 5, 6)
    element.mA shouldEqual ArrayA(MatrixUtils.fromVector(2, 2)(
      1, 0,
      0, 1
    ))
  }

}
