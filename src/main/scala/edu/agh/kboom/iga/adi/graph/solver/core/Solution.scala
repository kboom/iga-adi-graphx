package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.iga.adi.graph.solver.core.Spline.{Spline1T, Spline2T, Spline3T}
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix

case class Solution(m: IndexedRowMatrix, mesh: Mesh) {

  def getValue(x: Double, y: Double): Double = {
    val ielemx = (x / mesh.dx).toInt
    val ielemy = (y / mesh.dy).toInt
    val localx = x - mesh.dx * ielemx
    val localy = y - mesh.dy * ielemy

    println(f"Getting value {${ielemx},${ielemy}}")

    val c = Array.ofDim[Double](7, 7)

    return Spline1T.getValue(localx) * Spline1T.getValue(localy) * c(ielemx)(ielemy) +
      Spline1T.getValue(localx) * Spline2T.getValue(localy) * c(ielemx)(ielemy + 1) +
      Spline1T.getValue(localx) * Spline3T.getValue(localy) * c(ielemx)(ielemy + 2) +
      Spline2T.getValue(localx) * Spline1T.getValue(localy) * c(ielemx + 1)(ielemy) +
      Spline2T.getValue(localx) * Spline2T.getValue(localy) * c(ielemx + 1)(ielemy + 1) +
      Spline2T.getValue(localx) * Spline3T.getValue(localy) * c(ielemx + 1)(ielemy + 2) +
      Spline3T.getValue(localx) * Spline1T.getValue(localy) * c(ielemx + 2)(ielemy) +
      Spline3T.getValue(localx) * Spline2T.getValue(localy) * c(ielemx + 2)(ielemy + 1) +
      Spline3T.getValue(localx) * Spline3T.getValue(localy) * c(ielemx + 2)(ielemy + 2)
  }

}

object Solution {

  def asArray(s: Solution): Array[Array[Double]] = s.m
    .rows
    .sortBy(_.index)
    .map(_.vector.toArray)
    .collect()

  def asString(s: Solution): String = s.m
    .rows
    .sortBy(_.index)
    .map(_.vector.toArray.map(i => f"$i%+.3f").mkString(" "))
    .collect
    .mkString("\n")


  def print(s: Solution): Unit = {
    println(f"Matrix ${s.m.numRows()}x${s.m.numCols()}")
    println(asString(s))
  }

}
