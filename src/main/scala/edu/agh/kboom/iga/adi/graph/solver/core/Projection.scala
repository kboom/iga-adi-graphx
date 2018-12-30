package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.iga.adi.graph.solver.core.Spline.{Spline1T, Spline2T, Spline3T}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}

case class Projection(m: IndexedRowMatrix, mesh: Mesh)


object Projection {

  def asArray(s: Projection): Array[Array[Double]] = s.m
    .rows
    .sortBy(_.index)
    .map(_.vector.toArray)
    .collect()

  def asString(s: Projection): String = s.m
    .rows
    .map(_.vector.toArray.map(i => f"$i%+.3f").mkString("\t"))
    .collect
    .mkString(System.lineSeparator())


  def print(s: Projection): Unit = {
    println(f"2D B-Spline Coefficients ${s.m.numRows()}x${s.m.numCols()}")
    println(asString(s))
  }

  def valueRowsDependentOn(coefficientRow: Int)(implicit mesh: Mesh): Seq[Int] = {
    val elements = mesh.yDofs
    val size = mesh.xSize

    val all = Seq(-1, 0, 1)
      .map(_ + coefficientRow - 1)
      .filterNot { x => x < 0 || x >= size }

    val span = Math.min(3, 1 + Math.min(coefficientRow, elements - 1 - coefficientRow))

    return if (coefficientRow < elements / 2) all.take(span) else all.takeRight(span)
  }

  def surface(p: Projection)(implicit sc: SparkContext): IndexedRowMatrix = {
    implicit val mesh = p.mesh

    val coefficientsBySolutionRows = p.m.rows
      .flatMap(row => {
        val rid = row.index.toInt
        valueRowsDependentOn(rid)
          .map { element =>
            (element, (rid - element, row.vector.toArray))
          }
      })
      .groupBy(_._1.toLong)
      .mapValues(_.map(_._2))

    val surface = sc.parallelize(0 until mesh.ySize)
      .map(id => (id.toLong, id))
      .join(coefficientsBySolutionRows)
      .map { case (y, coefficients) => {
        val rows = coefficients._2.toMap
        val cols = 0 until mesh.xSize
        val row = Vectors.dense(cols.map(x => {
          val my = projectedValue((i, j) => rows(i)(j), x, y)
          println(f"For f($x,$y) = $my")
          my
        }).toArray)

        println(s"XX Vector $y: ${row.toArray.mkString(",")}")

        IndexedRow(y, row)
      }
      }

    return new IndexedRowMatrix(surface)
  }

  def projectedValue(c: (Int, Int) => Double, y: Double, x: Double)(implicit mesh: Mesh): Double = {
    val ielemx = (x / mesh.dx).toInt
    val ielemy = (y / mesh.dy).toInt
    val localx = x - mesh.dx * ielemx
    val localy = y - mesh.dy * ielemy

    c(0, ielemy) * Spline1T.getValue(localx) * Spline1T.getValue(localy) +
      c(0, ielemy + 1) * Spline1T.getValue(localx) * Spline2T.getValue(localy) +
      c(0, ielemy + 2) * Spline1T.getValue(localx) * Spline3T.getValue(localy) +
      c(1, ielemy) * Spline2T.getValue(localx) * Spline1T.getValue(localy) +
      c(1, ielemy + 1) * Spline2T.getValue(localx) * Spline2T.getValue(localy) +
      c(1, ielemy + 2) * Spline2T.getValue(localx) * Spline3T.getValue(localy) +
      c(2, ielemy) * Spline3T.getValue(localx) * Spline1T.getValue(localy) +
      c(2, ielemy + 1) * Spline3T.getValue(localx) * Spline2T.getValue(localy) +
      c(2, ielemy + 2) * Spline3T.getValue(localx) * Spline3T.getValue(localy)
  }

}
