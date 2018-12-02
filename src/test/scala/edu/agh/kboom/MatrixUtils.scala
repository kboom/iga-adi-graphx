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

  def indexedMatrix(r: Int, c: Int): Array[Array[Double]] = generatedMatrix(r, c)((rc, cc) => rc + (cc.toDouble / 100))

  def dummyMatrix(r: Int, c: Int): Array[Array[Double]] = generatedMatrix(r, c)(_ + _)

  def featuredMatrix(r: Int, c: Int)(f: Int*): Array[Array[Double]] = generatedMatrix(r, c)((rc, cc) => if (f.contains(c * rc + cc)) 1.0 else 0)

  def constMatrix(r: Int, c: Int)(o: Double = 1): Array[Array[Double]] = generatedMatrix(r, c)((_, _) => o)

  def unitMatrix(r: Int, c: Int): Array[Array[Double]] = constMatrix(r, c)(1)

  def constAMatrix(o: Double): MatrixA = MatrixA(generatedMatrix(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)((_, _) => o))

  def constBMatrix(o: Double)(implicit mesh: Mesh): MatrixB = MatrixB(generatedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)((_, _) => o))

  def constXMatrix(o: Double)(implicit mesh: Mesh): MatrixX = MatrixX(generatedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)((_, _) => o))

  def indexedAMatrix: MatrixA = MatrixA(indexedMatrix(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE))

  def indexedBMatrix(implicit mesh: Mesh): MatrixB = MatrixB(indexedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs))

  def indexedXMatrix(implicit mesh: Mesh): MatrixX = MatrixX(indexedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs))

  def dummyAMatrix(f: Int*): MatrixA = MatrixA(featuredMatrix(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)(f: _*))

  def dummyBMatrix(f: Int*)(implicit mesh: Mesh): MatrixB = MatrixB(featuredMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)(f: _*))

  def dummyXMatrix(f: Int*)(implicit mesh: Mesh): MatrixX = MatrixX(featuredMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)(f: _*))

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
