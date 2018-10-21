package edu.agh.kboom

import edu.agh.kboom.ProblemTree._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

case class Node(
                 start: Double = 0,
                 end: Double = 1,
                 mA: Array[Array[Double]] = Array.ofDim[Double](7, 7),
                 mB: Array[Array[Double]] = Array.ofDim[Double](7, 7),
                 mX: Array[Array[Double]] = Array.ofDim[Double](7, 7)
               )

object GraphXExample {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI GraphX").master("local[*]").getOrCreate()

    val sc = spark.sparkContext

    val problemTree = ProblemTree(12)

    val vertices: RDD[(VertexId, Node)] =
      sc.parallelize(Seq((0, Node())))

    val edges: RDD[Edge[Int]] =
      sc.parallelize(
        (1 to lastIndexOfPenultimateRow(problemTree)).flatMap(
          idx => childIndicesOf(idx)(problemTree).map(Edge(idx, _, 0))
        )
      )

    val dataItemGraph = Graph(vertices, edges, Node())

    val result =
      dataItemGraph.pregel(Node(), activeDirection = EdgeDirection.Out)(
        (_, vd, msg) => Node(), //msg + vd.start,
        t => Iterator((t.dstId, t.srcAttr)),
        (x, y) => Node()
      )

    result.vertices.collect().foreach(println)

    spark.stop()
  }

}
