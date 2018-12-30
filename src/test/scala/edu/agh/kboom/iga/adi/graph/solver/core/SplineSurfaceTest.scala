package edu.agh.kboom.iga.adi.graph.solver.core

import org.scalatest.prop.TableDrivenPropertyChecks._
import edu.agh.kboom.MethodSpec

class SplineSurfaceTest extends MethodSpec {

  describe("elementsDependentOnRow") {

    val problemSize = 12
    implicit val mesh = Mesh(problemSize, problemSize, problemSize, problemSize)

    forAll(Table(
      ("coefficientRow", "valueRow"),
      (0, Seq(0)),
      (1, Seq(0, 1)),
      (2, Seq(0, 1, 2)),
      (3, Seq(1, 2, 3)),
      (4, Seq(2, 3, 4)),
      (10, Seq(8, 9, 10)),
      (11, Seq(9, 10, 11)),
      (12, Seq(10, 11)),
      (13, Seq(11))
    )) { (coefficientRow: Int, valueRow: Seq[Int]) =>
      it(f"is ${valueRow.mkString("(", ",", ")")} for $coefficientRow") {
        SplineSurface.valueRowsDependentOn(coefficientRow) should contain theSameElementsAs valueRow
      }
    }

  }
}
