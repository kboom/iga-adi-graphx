package edu.agh.kboom

import edu.agh.kboom.ProblemTree._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


object IgaAdiGraphXSolver {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI GraphX").master("local[*]").getOrCreate()

    val sc = spark.sparkContext

    val problemTree = ProblemTree(6144)

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
        VertexProgram.run,
        VertexProgram.sendMsg,
        VertexProgram.mergeMsg
      )

    result.vertices.collect().foreach(println)

    spark.stop()
  }

}
