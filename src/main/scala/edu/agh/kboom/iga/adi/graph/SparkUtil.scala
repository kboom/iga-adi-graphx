package edu.agh.kboom.iga.adi.graph

import org.apache.spark.rdd.RDD

object SparkUtil {

  def elementsByPartitions[A](m: RDD[A]): Map[Int, Int] =
    m.map { case (id, attr) => (id, attr) }
      .mapPartitionsWithIndex {
        case (i, rows) => Iterator((i, rows.size))
      }.collectAsMap().toMap

}
