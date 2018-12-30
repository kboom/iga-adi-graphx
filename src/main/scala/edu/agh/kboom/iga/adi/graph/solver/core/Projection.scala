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
    .sortBy(_.index)
    .map(_.vector.toArray.map(i => f"$i%+.3f").mkString(" "))
    .collect
    .mkString("\n")


  def print(s: Projection): Unit = {
    println(f"Matrix ${s.m.numRows()}x${s.m.numCols()}")
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

    println("Coefficients by solution rows:\n" + coefficientsBySolutionRows.collect().map {
      case (vid, value) => f"$vid: ${value.map(v => v._1 + ": " + v._2.take(3).map(x => f"$x%.2f").mkString(","))}"
    }.mkString(System.lineSeparator()))

    val surface = sc.parallelize(0 until mesh.yDofs)
      .map(id => (id.toLong, id))
      .join(coefficientsBySolutionRows)
      .map { case (y, coefficients) => {
        val rows = coefficients._2.toMap
        val fromRows: (Int, Int) => Double = (i, j) => rows(i)(j)
        val cols = 0 until mesh.xDofs
        val row = Vectors.dense(cols.map(x => projectedValue(fromRows, x, y)).toArray)
        IndexedRow(y, row)
      }}

    return new IndexedRowMatrix(surface)
  }

  def projectedValue(c: (Int, Int) => Double, x: Double, y: Double)(implicit mesh: Mesh): Double = {
    val ielemx = (x / mesh.dx).toInt
    val ielemy = (y / mesh.dy).toInt
    val localx = x - mesh.dx * ielemx
    val localy = y - mesh.dy * ielemy

    Spline1T.getValue(localx) * Spline1T.getValue(localy) * c(0, 0) +
      Spline1T.getValue(localx) * Spline2T.getValue(localy) * c(0, 1) +
      Spline1T.getValue(localx) * Spline3T.getValue(localy) * c(0, 2) +
      Spline2T.getValue(localx) * Spline1T.getValue(localy) * c(1, 0) +
      Spline2T.getValue(localx) * Spline2T.getValue(localy) * c(1, 1) +
      Spline2T.getValue(localx) * Spline3T.getValue(localy) * c(1, 2) +
      Spline3T.getValue(localx) * Spline1T.getValue(localy) * c(2, 0) +
      Spline3T.getValue(localx) * Spline2T.getValue(localy) * c(2, 1) +
      Spline3T.getValue(localx) * Spline3T.getValue(localy) * c(2, 2)
  }

}
