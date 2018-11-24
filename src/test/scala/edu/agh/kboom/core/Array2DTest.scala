package edu.agh.kboom.core

import org.scalatest.{FunSpec, Matchers}

sealed case class MyArray(arr: Array[Array[Double]]) extends Array2D[MyArray] {
  override def create(v: Array[Array[Double]]): MyArray = MyArray(v)

  override def toString: String = "\n" + arr.map(_.mkString(",")).mkString("\n") + "\n"
}

object MyArray {
  def fromVector(r: Int, c: Int)(cells: Double*): MyArray = {
    val arr = Array.ofDim[Double](r, c)
    for (i <- 0 until r) {
      arr(i) = cells.slice(i * c, (i + 1) * c).toArray
    }
    new MyArray(arr)
  }
}

sealed class Array2DTest extends FunSpec with Matchers {

  it("Can add matrices") {
    MyArray.fromVector(2, 2)(
      1, 0,
      0, 1
    ) + MyArray.fromVector(2, 2)(
      0, 1,
      1, 0
    ) shouldBe MyArray.fromVector(2, 2)(
      1, 1,
      1, 1
    )
  }

}
