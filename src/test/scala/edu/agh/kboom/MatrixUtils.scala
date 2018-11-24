package edu.agh.kboom

object MatrixUtils {
  def fromVector(r: Int, c: Int)(cells: Double*): Array[Array[Double]] = {
    val arr = Array.ofDim[Double](r, c)
    for (i <- 0 until r) {
      arr(i) = cells.slice(i * c, (i + 1) * c).toArray
    }
    arr
  }
}
