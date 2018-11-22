package edu.agh.kboom.core

trait ArrayOperation {
  def map(r: Int, c: Int): (Int, Int)
}

sealed case class MoveOperation(down: Int, right: Int) extends ArrayOperation {
  override def map(r: Int, c: Int): (Int, Int) = (r + down, c + right)
}

abstract class Array2D[T](arr: Array[Array[Double]]) {

  def replace(r: Int, c: Int, v: Double): Array2D[T] = {
    arr(r)(c) = v
    this
  }

  def replace(r: Int, c: Int)(vm: Double => Double): Array2D[T] = replace(r, c, vm(arr(r)(c)))

  def row(r: Int): Array[Double] = arr(r)

  def select(r: Int, c: Int, s: Int): T = select(r, s, c, s)

  def select(r: Int, rs: Int, c: Int, cs: Int): T = {
    def sub = Array.ofDim[Double](rs, cs)

    for (row <- 1 to rs) {
      for (col <- c to cs) {
        sub(row)(col) = arr(row + r)(col + c)
      }
    }
    create(sub)
  }

  def add(other: Array2D[T]): Unit = {
    val rows = arr.length
    val cols = arr(0).length

    for (r <- 1 to rows) {
      for (c <- 1 to cols) {
        arr(r)(c) += other(r)(c)
      }
    }
  }

  def create(v: Array[Array[Double]]): T

  def transformedBy(rr: Range, cr: Range)(extract: ArrayOperation*)(insert: ArrayOperation*): T = {
    val rows = arr.length
    val cols = arr(0).length

    def sub = Array.ofDim[Double](rows, cols)

    for (r <- rr) {
      for (c <- cr) {

        val (dr, dc) = insert.aggregate(
          (r, c)
        )(
          (x, a) => a.map(x._1, x._2),
          (a, b) => (a._1 + b._1, a._2 + b._2)
        )

        val (sr, sc) = extract.aggregate(
          (r, c)
        )(
          (x, a) => a.map(x._1, x._2),
          (a, b) => (a._1 + b._1, a._2 + b._2)
        )

        sub(dr)(dc) = sub(sr)(sc)
      }
    }
    create(sub)
  }

  def +(that: Array2D[T]): T = {
    val rows = arr.length
    val cols = arr(0).length

    def sub = Array.ofDim[Double](rows, cols)

    for (r <- 1 to rows) {
      for (c <- 1 to cols) {
        sub(r)(c) += arr(r)(c) + that(r)(c)
      }
    }
    create(sub)
  }

  def swap(r1: Int, c1: Int, r2: Int, c2: Int): Unit = {
    val tmp = arr(r1)(c1)
    arr(r1)(c1) = arr(r2)(c2)
    arr(r2)(c2) = tmp
  }

  def apply(r: Int)(c: Int): Double = arr(r)(c)

}

sealed case class ArrayA(v: Array[Array[Double]]) extends Array2D[ArrayA](v) {
  override def create(v: Array[Array[Double]]): ArrayA = new ArrayA(v)
}

sealed case class ArrayB(v: Array[Array[Double]]) extends Array2D[ArrayB](v) {
  override def create(v: Array[Array[Double]]): ArrayB = new ArrayB(v)
}

sealed case class ArrayX(v: Array[Array[Double]]) extends Array2D[ArrayX](v) {
  override def create(v: Array[Array[Double]]): ArrayX = new ArrayX(v)
}


object Array2D {
  def moveToDest(down: Int, right: Int): ArrayOperation = MoveOperation(down, right)

  def moveFromSource(up: Int, left: Int): ArrayOperation = MoveOperation(-up, -left)
}

object ArrayA {
  def ofDim(rows: Int, cols: Int): ArrayA = ArrayA(Array.ofDim[Double](rows, cols))
}

object ArrayB {
  def ofDim(rows: Int, cols: Int): ArrayB = ArrayB(Array.ofDim[Double](rows, cols))
}

object ArrayX {
  def ofDim(rows: Int, cols: Int): ArrayX = ArrayX(Array.ofDim[Double](rows, cols))
}
