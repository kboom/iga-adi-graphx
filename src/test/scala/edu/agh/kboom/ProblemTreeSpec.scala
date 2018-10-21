package edu.agh.kboom

import edu.agh.kboom.ProblemTree._
import org.scalatest._

class ProblemTreeSpec extends FlatSpec {

  "total height of 12 element tree" should "be 4" in {
    assert(totalHeightOf(ProblemTree(12)) == 4)
  }

  "regular height of 12 element tree" should "be 3" in {
    assert(regularHeightOf(ProblemTree(12)) == 3)
  }

  "first index of penultimate row of 12 element tree" should "be 4" in {
    assert(firstIndexOfPenultimateRow(ProblemTree(12)) == 4)
  }

  "last index of penultimate row of 12 element tree" should "be 7" in {
    assert(lastIndexOfPenultimateRow(ProblemTree(12)) == 7)
  }

  "first index of last row of 12 element tree" should "be 8" in {
    assert(firstIndexOfLastRow(ProblemTree(12)) == 8)
  }

  "last index of last row of 12 element tree" should "be 19" in {
    assert(lastIndexOfLastRow(ProblemTree(12)) == 19)
  }

  "left child of 3 in 12 element tree" should "be 6" in {
    assert(leftChildOf(3)(ProblemTree(12)).get == 6)
  }

  "left child of 4 in 12 element tree" should "be 8" in {
    assert(leftChildOf(4)(ProblemTree(12)).get == 8)
  }

  "left child of 7 in 12 element tree" should "be 17" in {
    assert(leftChildOf(7)(ProblemTree(12)).get == 17)
  }

  "children of 2 in 12 element tree" should "be 4 and 5" in {
    assert(childIndicesOf(2)(ProblemTree(12)) == Seq(4, 5))
  }

  "children of 3 in 12 element tree" should "be 6 and 7" in {
    assert(childIndicesOf(3)(ProblemTree(12)) == Seq(6, 7))
  }

  "children of 4 in 12 element tree" should "be 8, 9, 10" in {
    assert(childIndicesOf(4)(ProblemTree(12)) == Seq(8, 9, 10))
  }

  "children of 5 in 12 element tree" should "be 11, 12, 13" in {
    assert(childIndicesOf(5)(ProblemTree(12)) == Seq(11, 12, 13))
  }

}
