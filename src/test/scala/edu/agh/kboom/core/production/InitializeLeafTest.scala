package edu.agh.kboom.core.production

import edu.agh.kboom.ElementUtils.elementBoundTo
import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom.{DummyProblem, MatrixColors, SubjectSpec}

class InitializeLeafTest extends SubjectSpec
  with DummyProblem
  with MatrixColors {

  "initialize" when {

    val element = elementBoundTo(TestMesh, Parent)()

    "is not empty" in {
      InitializeLeaf.initialize(element)
      weakPrecision(element.mA) shouldBe matrixA(
        +01.00, +00.00, +00.00, +00.00, +00.00, +00.00,
        +00.00, +00.38, -00.06, -00.16, +00.00, +00.00,
        +00.00, -00.06, +00.78, -00.06, +00.00, +00.00,
        +00.00, -00.16, -00.06, +00.38, +00.00, +00.00,
        +00.00, +00.00, +00.00, +00.00, +01.00, +00.00,
        +00.00, +00.00, +00.00, +00.00, +00.00, +01.00
      )
    }

  }

}
