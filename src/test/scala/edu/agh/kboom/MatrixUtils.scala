package edu.agh.kboom

import edu.agh.kboom.core._
import edu.agh.kboom.core.tree.Element.{COLS_BOUND_TO_NODE, ROWS_BOUND_TO_NODE}

sealed case class TestMatrix(arr: Array[Array[Double]]) extends Array2D[TestMatrix] {
  override def create(v: Array[Array[Double]]): TestMatrix = TestMatrix(v)
}

object MatrixUtils {

  def fromVector(r: Int, c: Int)(cells: Double*): Array[Array[Double]] = {
    val arr = Array.ofDim[Double](r, c)
    for (i <- 0 until r) {
      arr(i) = cells.slice(i * c, (i + 1) * c).toArray
    }
    arr
  }

  def identityMatrix(size: Int): Array[Array[Double]] = {
    val arr = Array.ofDim[Double](size, size)
    for (i <- 0 until size) {
      arr(i)(i) = 1
    }
    arr
  }

  def indexedSquareMatrix(size: Int): Array[Array[Double]] = indexedMatrix(size, size)

  def indexedMatrix(r: Int, c: Int): Array[Array[Double]] = generatedMatrix(r, c)((rc, cc) => rc * c + cc)

  def dummyMatrix(r: Int, c: Int) : Array[Array[Double]] = generatedMatrix(r, c)(_ + _)

  def dummyAMatrix(seed: Int = 1, generator: (Int, Int) => Double = _ + _): MatrixA = MatrixA(generatedMatrix(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)(seed + generator(_, _)))

  def dummyBMatrix(seed: Int = 1, generator: (Int, Int) => Double = _ + _)(implicit mesh: Mesh): MatrixB = MatrixB(generatedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)(seed + generator(_, _)))

  def dummyXMatrix(seed: Int = 1, generator: (Int, Int) => Double = _ + _)(implicit mesh: Mesh): MatrixX = MatrixX(generatedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)(seed + generator(_, _)))

  def generatedMatrix(r: Int, c: Int)(gen: (Int, Int) => Double): Array[Array[Double]] = {
    val arr = Array.ofDim[Double](r, c)
    for {
      ri <- 0 until r
      ci <- 0 until c
    } {
      arr(ri)(ci) = gen(ri, ci)
    }
    arr
  }

}
