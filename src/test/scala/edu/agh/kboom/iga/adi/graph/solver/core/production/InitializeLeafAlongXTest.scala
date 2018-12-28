package edu.agh.kboom.iga.adi.graph.solver.core.production

import edu.agh.kboom.ElementUtils.cleanElementBoundTo
import edu.agh.kboom.MatrixUtils._
import edu.agh.kboom.iga.adi.graph.solver.core.tree.BranchVertex
import edu.agh.kboom.{DummyProblem, MatrixColors, SubjectSpec}

class InitializeLeafAlongXTest extends SubjectSpec
  with DummyProblem
  with MatrixColors {

  val Parent = BranchVertex(4)

//  "initialize" when {
//
//    "sets stiffness matrix to valid coefficients" in {
//      val element = cleanElementBoundTo(TestMesh, Parent)()
//      InitializeLeafAlongX.initialize(element)
//      weakPrecision(element.mA) shouldBe matrixA(
//        +00.05, +00.11, +00.01, +00.00, +00.00, +00.00,
//        +00.11, +00.45, +00.11, +00.00, +00.00, +00.00,
//        +00.01, +00.11, +00.05, +00.00, +00.00, +00.00,
//        +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
//        +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
//        +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
//      )
//    }
//
//    "sets forcing matrix to valid coefficients" in {
//      val element = cleanElementBoundTo(TestMesh, Parent)()
//      InitializeLeafAlongX.initialize(element)
//      weakPrecision(element.mB) shouldBe matrixB(
//        +00.03, +00.14, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.14, +00.03,
//        +00.11, +00.56, +00.67, +00.67, +00.67, +00.67, +00.67, +00.67, +00.67, +00.67, +00.67, +00.67, +00.56, +00.11,
//        +00.03, +00.14, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.17, +00.14, +00.03,
//        +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
//        +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00,
//        +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00, +00.00
//      )
//    }
//
//  }

}
