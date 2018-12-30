package edu.agh.kboom.iga.adi.graph.solver.core

import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix
import edu.agh.kboom.iga.adi.graph.solver.core.Spline.{Spline1T, Spline2T, Spline3T}

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
