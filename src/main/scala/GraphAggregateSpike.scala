import org.apache.spark.graphx.{Graph, VertexRDD}
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.sql.SparkSession

object GraphAggregateSpike {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    val sc = spark.sparkContext

    // Create a graph with "age" as the vertex property.
    // Here we use a random graph for simplicity.
    val graph: Graph[Double, Int] =
    GraphGenerators.logNormalGraph(sc, numVertices = 100).mapVertices((id, _) => id.toDouble)
    // Compute the number of older followers and their total age
    val olderFollowers: VertexRDD[(Int, Double)] = graph.aggregateMessages[(Int, Double)](
      triplet => { // Map Function
        if (triplet.srcAttr > triplet.dstAttr) {
          // Send message to destination vertex containing counter and age
          triplet.sendToDst((1, triplet.srcAttr))
        }
      },
      // Add counter and age
      (a, b) => (a._1 + b._1, a._2 + b._2) // Reduce Function
    )
    // Divide total age by number of older followers to get average age of older followers
    val avgAgeOfOlderFollowers: VertexRDD[Double] =
      olderFollowers.mapValues((id, value) =>
        value match {
          case (count, totalAge) => totalAge / count
        })

    // Display the results
    avgAgeOfOlderFollowers.collect.foreach(println(_))
  }

}
