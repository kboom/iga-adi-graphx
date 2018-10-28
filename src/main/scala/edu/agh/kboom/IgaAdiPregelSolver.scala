package edu.agh.kboom

import edu.agh.kboom.production.Ax
import edu.agh.kboom.tree.ProblemTree._
import edu.agh.kboom.tree.Vertex._
import edu.agh.kboom.tree.{Element, InterimVertex, ProblemTree, Vertex}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    val sc = spark.sparkContext

    val problemTree = ProblemTree(12)
    val igaMesh = Mesh(12, 12, 12, 12)

    val vertices: RDD[(VertexId, Element)] =
      sc.parallelize(Seq((1, Element.createForX(igaMesh))))

    val edges: RDD[Edge[IgaOperation]] =
      sc.parallelize(
        (1 to lastIndexOfBranchingRow(problemTree)).flatMap(
          idx => childIndicesOf(vertexOf(idx)(problemTree))(problemTree).map(v => Edge(idx, v.id, IgaOperation.operationFor(Vertex.vertexOf(idx)(problemTree), v)))
        )
      )

    val dataItemGraph = Graph(vertices, edges, Element.createForX(igaMesh))

    implicit val program: VertexProgram = VertexProgram(IgaContext(igaMesh, (x, y) => 1))

    val result =
      dataItemGraph.pregel(IgaMessage(Ax()), activeDirection = EdgeDirection.In)(
        VertexProgram.run,
        VertexProgram.sendMsg,
        VertexProgram.mergeMsg
      )

    result.vertices.collect().foreach(println)

    spark.stop()
  }

}
