package edu.agh.kboom.iga.adi.graph.solver.core

import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix

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

}
