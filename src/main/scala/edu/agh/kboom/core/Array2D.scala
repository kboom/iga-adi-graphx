package edu.agh.kboom.core

trait ArrayOperation {
  def map(r: Int, c: Int): (Int, Int)
}

sealed case class MoveOperation(down: Int, right: Int) extends ArrayOperation {
  override def map(r: Int, c: Int): (Int, Int) = (r + down, c + right)
}

case object IdentityOperation extends ArrayOperation {
  override def map(r: Int, c: Int): (Int, Int) = (r, c)
}

trait Array2D[T] extends Serializable {

  val arr: Array[Array[Double]]

  def replace(r: Int, c: Int, v: Double): Array2D[T] = {
    arr(r)(c) = v
    this
  }

  def replace(r: Int, c: Int)(vm: Double => Double): Array2D[T] = replace(r, c, vm(arr(r)(c)))

  def row(r: Int): Array[Double] = arr(r)

  def select(r: Int, c: Int, s: Int): T = select(r, s, c, s)

  def select(r: Int, rs: Int, c: Int, cs: Int): T = {
    val sub = Array.ofDim[Double](rs, cs)

    for (row <- 1 until rs) {
      for (col <- c until cs) {
        sub(row)(col) = arr(row + r)(col + c)
      }
    }
    create(sub)
  }

  def rowCount: Int = arr.length

  def colCount: Int = arr(0).length

  def add(other: Array2D[T]): Unit = {
    if(rowCount != other.rowCount || colCount != other.colCount)
      throw new IllegalArgumentException(s"Matrix dimensions ${rowCount}x$colCount do not match ${other.rowCount}x${other.colCount}")

    for (r <- 0 until rowCount) {
      for (c <- 0 until colCount) {
        arr(r)(c) += other(r)(c)
      }
    }
  }

  def create(v: Array[Array[Double]]): T

  def transformedBy(rr: Range, cr: Range)(extract: ArrayOperation*)(insert: ArrayOperation*): T = {
    val rows = arr.length
    val cols = arr(0).length
    val sub = Array.ofDim[Double](rows, cols)

    for (r <- rr) {
      for (c <- cr) {

        val (dr, dc) = insert.aggregate((r, c))(
          (x, a) => a.map(x._1, x._2),
          (a, b) => (a._1 + b._1, a._2 + b._2)
        )

        val (sr, sc) = extract.aggregate((r, c))(
          (x, a) => a.map(x._1, x._2),
          (a, b) => (a._1 + b._1, a._2 + b._2)
        )

        sub(dr)(dc) = arr(sr)(sc)
      }
    }
    create(sub)
  }

  def +(that: Array2D[T]): T = {
    val rows = arr.length
    val cols = arr(0).length
    val sub = Array.ofDim[Double](rows, cols)

    for {
      r <- 0 until rows
      c <- 0 until cols
    } {
      sub(r)(c) = arr(r)(c) + that(r)(c)
    }

    create(sub)
  }

  def swap(r1: Int, c1: Int, r2: Int, c2: Int): Unit = {
    val tmp = arr(r1)(c1)
    arr(r1)(c1) = arr(r2)(c2)
    arr(r2)(c2) = tmp
  }

  def apply(r: Int)(c: Int): Double = arr(r)(c)

  override def equals(obj: scala.Any): Boolean = obj match {
    case array: Array2D[Double] =>
      array.arr.deep == arr.deep
    case _ =>
      false
  }

  override def toString: String = "\n" + arr.map(_.map(_.formatted("%+1.2f")).mkString(", ")).mkString("\n") + "\n"

}

sealed case class MatrixA(arr: Array[Array[Double]]) extends Array2D[MatrixA] {
  override def create(v: Array[Array[Double]]): MatrixA = new MatrixA(v)
}

sealed case class MatrixB(arr: Array[Array[Double]]) extends Array2D[MatrixB] {
  override def create(v: Array[Array[Double]]): MatrixB = new MatrixB(v)
}

sealed case class MatrixX(arr: Array[Array[Double]]) extends Array2D[MatrixX] {
  override def create(v: Array[Array[Double]]): MatrixX = new MatrixX(v)
}


object Array2D {
  def moveToDest(down: Int, right: Int): ArrayOperation = MoveOperation(down, right)

  def moveFromSource(up: Int, left: Int): ArrayOperation = MoveOperation(-up, -left)
}

object MatrixA {
  def ofDim(rows: Int, cols: Int): MatrixA = MatrixA(Array.ofDim[Double](rows, cols))
}

object MatrixB {
  def ofDim(rows: Int, cols: Int): MatrixB = MatrixB(Array.ofDim[Double](rows, cols))
}

object MatrixX {
  def ofDim(rows: Int, cols: Int): MatrixX = MatrixX(Array.ofDim[Double](rows, cols))
}
