package edu.agh.kboom

import edu.agh.kboom.iga.adi.graph.solver.core._
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Element.{COLS_BOUND_TO_NODE, ROWS_BOUND_TO_NODE}

sealed case class TestMatrix(arr: Array[Array[Double]]) extends Array2D[TestMatrix] {
  override def create(v: Array[Array[Double]]): TestMatrix = TestMatrix(v)
}

object MatrixUtils {

  def weakPrecision[T](m: Array2D[T]): T = m.mappedBy((_, _, v) => BigDecimal(v).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)

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

  def unit: (Int, Int) => Double = fill(1)
  def fill(v: Double): (Int, Int) => Double = (_, _) => v
  def entry(r: Int, c: Int)(v: Double): (Int, Int) => Double = (tr, tc) => if(tr == r && tc == c) v else 0
  def index: (Int, Int) => Double = (tr, tc) => tr + (tc.toDouble / 100)
  def identity: (Int, Int) => Double = (tr, tc) => if(tr == tc) 1 else 0

  def indexedSquareMatrix(size: Int): Array[Array[Double]] = indexedMatrix(size, size)

  def indexedMatrix(r: Int, c: Int): Array[Array[Double]] = generatedMatrix(r, c)((rc, cc) => rc + (cc.toDouble / 100))

  def dummyMatrix(r: Int, c: Int): Array[Array[Double]] = generatedMatrix(r, c)(_ + _)

  def featuredMatrix(r: Int, c: Int)(f: Int*): Array[Array[Double]] = generatedMatrix(r, c)((rc, cc) => if (f.contains(c * rc + cc)) 1.0 else 0)

  def matrixOf(r: Int, c: Int)(m: Array[Array[Double]]*) : Array[Array[Double]] = generatedMatrix(r, c)((rc, cc) => m.map(_ (rc)(cc)).sum)

  def constMatrix(r: Int, c: Int)(o: Double = 1): Array[Array[Double]] = generatedMatrix(r, c)((_, _) => o)

  def unitMatrix(r: Int, c: Int): Array[Array[Double]] = constMatrix(r, c)(1)

  def constAMatrix(o: Double): MatrixA = MatrixA(generatedMatrix(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)((_, _) => o))

  def constBMatrix(o: Double)(implicit mesh: Mesh): MatrixB = MatrixB(generatedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)((_, _) => o))

  def constXMatrix(o: Double)(implicit mesh: Mesh): MatrixX = MatrixX(generatedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)((_, _) => o))

  def indexedAMatrix: MatrixA = MatrixA(indexedMatrix(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE))

  def indexedBMatrix(implicit mesh: Mesh): MatrixB = MatrixB(indexedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs))

  def indexedXMatrix(implicit mesh: Mesh): MatrixX = MatrixX(indexedMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs))

  def featuredAMatrix(f: Int*): MatrixA = MatrixA(featuredMatrix(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)(f: _*))

  def featuredBMatrix(f: Int*)(implicit mesh: Mesh): MatrixB = MatrixB(featuredMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)(f: _*))

  def featuredXMatrix(f: Int*)(implicit mesh: Mesh): MatrixX = MatrixX(featuredMatrix(ROWS_BOUND_TO_NODE, mesh.xDofs)(f: _*))


  def generatedMatrixA(g: Seq[(Int, Int) => Double]): MatrixA
  = MatrixA(assembleMatrix(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)(g))

  def generatedMatrixB(g: Seq[(Int, Int) => Double])(implicit m: Mesh): MatrixB
  = MatrixB(assembleMatrix(ROWS_BOUND_TO_NODE, m.xDofs)(g))

  def generatedMatrixX(g: Seq[(Int, Int) => Double])(implicit m: Mesh): MatrixX
  = MatrixX(assembleMatrix(ROWS_BOUND_TO_NODE, m.xDofs)(g))

  def matrixA(a: Double*): MatrixA
  = MatrixA(fromVector(ROWS_BOUND_TO_NODE, COLS_BOUND_TO_NODE)(a: _*))

  def matrixB(a: Double*)(implicit m: Mesh): MatrixB
  = MatrixB(fromVector(ROWS_BOUND_TO_NODE, m.xDofs)(a: _*))

  def matrixX(a: Double*)(implicit m: Mesh): MatrixX
  = MatrixX(fromVector(ROWS_BOUND_TO_NODE, m.xDofs)(a: _*))


  def assembleMatrix(r: Int, c: Int)(g: Seq[(Int, Int) => Double]): Array[Array[Double]] = {
    val arr = Array.ofDim[Double](r, c)
    for {
      ri <- 0 until r
      ci <- 0 until c
    } {
      arr(ri)(ci) = g.map(_.apply(ri, ci)).sum
    }
    arr
  }

  @Deprecated
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
