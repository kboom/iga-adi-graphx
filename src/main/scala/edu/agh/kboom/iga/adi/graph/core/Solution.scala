package edu.agh.kboom.iga.adi.graph.core

import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix

case class Solution(m: IndexedRowMatrix)

object Solution {

  def print(s: Solution): Unit = {
    println(f"Matrix ${s.m.numRows()}x${s.m.numCols()}")
    s.m
      .rows // Extract RDD[org.apache.spark.mllib.linalg.Vector]
      .sortBy(_.index)
      .map(_.vector.toArray.map(i => f"$i%+.3f").mkString(" "))
      .collect // you can use toLocalIterator to limit memory usage
      .foreach(println) // Iterate over local Iterator and print
  }

}
