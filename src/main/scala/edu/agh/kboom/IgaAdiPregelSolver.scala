package edu.agh.kboom

import edu.agh.kboom.core.production.{InitializeLeafMessage, ProductionMessage}
import edu.agh.kboom.core.tree._
import edu.agh.kboom.core.{IgaContext, IgaOperation, IgaTasks, Mesh}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    val sc = spark.sparkContext

    val problemTree = ProblemTree(12)
    val igaMesh = Mesh(12, 12, 12, 12)

    val edges: RDD[Edge[IgaOperation]] =
      sc.parallelize(
        IgaTasks.generateOperations(problemTree)
          .map(e => Edge(e.src.id, e.dst.id, e))
      )

    val dataItemGraph = Graph.fromEdges(edges, None)
      .mapVertices((vid, _) => IgaElement(Vertex.vertexOf(vid.toInt)(problemTree), Element.createForX(igaMesh)))

    implicit val program: VertexProgram = VertexProgram(IgaContext(igaMesh, (x, y) => 1))

    val initialMessage = InitializeLeafMessage().asInstanceOf[ProductionMessage]

    val result =
      dataItemGraph.pregel(initialMessage, activeDirection = EdgeDirection.Out)(
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
