package edu.agh.kboom.core.production

import edu.agh.kboom.ElementUtils.elementBoundTo
import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom.core.tree.BranchVertex
import edu.agh.kboom.{DummyProblem, MatrixColors, SubjectSpec}

class InitializeLeafTest extends SubjectSpec
  with DummyProblem
  with MatrixColors {

  val Parent = BranchVertex(4)

  "initialize" when {

    val element = elementBoundTo(TestMesh, Parent)()

    "sets stiffness matrix to valid coefficients" in {
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

    "sets forcing matrix to valid coefficients" in {
      InitializeLeaf.initialize(element)
      weakPrecision(element.mB) shouldBe matrixB(
        +00.00, +00.01, +00.02, +00.03, +00.04, +00.05, +00.06, +00.07, +00.08, +00.09, +00.10, +00.11, +00.12, +00.13,
        +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.20,
        +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.21,
        +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
        +04.00, +04.01, +04.02, +04.03, +04.04, +04.05, +04.06, +04.07, +04.08, +04.09, +04.10, +04.11, +04.12, +04.13,
        +05.00, +05.01, +05.02, +05.03, +05.04, +05.05, +05.06, +05.07, +05.08, +05.09, +05.10, +05.11, +05.12, +05.13
      )
    }

  }

}
