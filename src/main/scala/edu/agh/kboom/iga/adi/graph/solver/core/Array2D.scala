package edu.agh.kboom.iga.adi.graph.solver.core

import breeze.linalg.DenseMatrix
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA

object MatrixUtil {

  implicit class DenseMatrixUtil(val d: DenseMatrix[Double]) {

    def swapRows(r1: Int, r2: Int): DenseMatrix[Double] = {
      val old = d(r1, ::).inner.copy.t
      d(r1, ::) := d(r2, ::)
      d(r2, ::) := old
      d
    }

    /*
    todo This might be unnecessary - :: can be more efficient
     */
    def swapRows(r1: Int, r2: Int, size: Int): DenseMatrix[Double] = {
      val old = d(r1, 0 until size).inner.copy.t
      d(r1, 0 until size) := d(r2, 0 until size)
      d(r2, 0 until size) := old
      d
    }

    /*
    todo This might be unnecessary - :: can be more efficient
     */
    def swapCols(c1: Int, c2: Int, size: Int): DenseMatrix[Double] = {
      val old = d(0 until size, c1).copy
      d(0 until size, c1) := d(0 until size, c2)
      d(0 until size, c2) := old
      d
    }

    def swapCols(c1: Int, c2: Int): DenseMatrix[Double] = {
      val old = d(::, c1).copy
      d(::, c1) := d(::, c2)
      d(::, c2) := old
      d
    }

    def ::+(o: DenseMatrix[Double]): DenseMatrix[Double] = {
      this.d += o
      this.d
    }

  }

}

object MatrixFactory {

  def ofDim(a: MatrixA)(f: DenseMatrix[Double] => Unit): DenseMatrix[Double] = {
    val b = DenseMatrix.zeros[Double](a.rows, a.cols)
    f(b)
    b
  }

}

object MatrixA {
  type MatrixA = DenseMatrix[Double]

  def ofDim(rows: Int, cols: Int): MatrixA = DenseMatrix.zeros(rows, cols)

  def ofDim(m: MatrixA): MatrixA = DenseMatrix.zeros(m.rows, m.cols)

}

object MatrixB {
  type MatrixB = DenseMatrix[Double]

  def ofDim(rows: Int, cols: Int): MatrixB = DenseMatrix.zeros(rows, cols)

  def ofDim(m: MatrixB): MatrixA = DenseMatrix.zeros(m.rows, m.cols)
}

object MatrixX {
  type MatrixX = DenseMatrix[Double]

  def ofDim(rows: Int, cols: Int): MatrixX = DenseMatrix.zeros(rows, cols)

  def ofDim(m: MatrixX): MatrixX = DenseMatrix.zeros(m.rows, m.cols)
}
