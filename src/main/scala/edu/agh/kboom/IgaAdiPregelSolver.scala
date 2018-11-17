package edu.agh.kboom

import edu.agh.kboom.core.production.{InitializeLeafMessage, ProductionMessage}
import edu.agh.kboom.core.tree.ProblemTree._
import edu.agh.kboom.core.tree.Vertex._
import edu.agh.kboom.core.tree._
import edu.agh.kboom.core.{IgaContext, IgaOperation, Mesh}
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
        (1 to lastIndexOfLeafRow(problemTree)).flatMap(
          idx => childIndicesOf(vertexOf(idx)(problemTree))(problemTree).flatMap(
            v1 => Some(Vertex.vertexOf(idx)(problemTree)).map(v2 => Seq(
              IgaOperation.operationFor(v1, v2).map(Edge(v1.id, v2.id, _)),
              IgaOperation.operationFor(v2, v1).map(Edge(v2.id, v1.id, _))
            )).map(_.flatten)
          ).flatten
        )
      )

    val dataItemGraph = Graph(vertices, edges, Element.createForX(igaMesh))

    implicit val program: VertexProgram = VertexProgram(IgaContext(igaMesh, (x, y) => 1))

    val initialMessage = InitializeLeafMessage().asInstanceOf[ProductionMessage]

    val result =
      dataItemGraph.pregel(initialMessage, activeDirection = EdgeDirection.In)(
        VertexProgram.run,
        VertexProgram.sendMsg,
        VertexProgram.mergeMsg
      )

    result.vertices.collect().foreach(println)


    // transpose the matrix

    // https://stackoverflow.com/questions/30556478/matrix-transpose-on-rowmatrix-in-spark

    spark.stop()
  }

}
