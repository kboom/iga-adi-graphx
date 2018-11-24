import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.graphx.{Graph}
import org.apache.spark.sql.SparkSession

object VertexMatrixExtractionSpike {

//  def transposeRowMatrix(m: RowMatrix): RowMatrix = {
//    val transposedRowsRDD = m.rows.zipWithIndex.map{case (row, rowIndex) => rowToTransposedTriplet(row, rowIndex)}
//      .flatMap(x => x) // now we have triplets (newRowIndex, (newColIndex, value))
//      .groupByKey
//      .sortByKey().map(_._2) // sort rows and remove row indexes
//      .map(buildRow) // restore order of elements in each row and remove column indexes
//    new RowMatrix(transposedRowsRDD)
//  }
//
//  def rowToTransposedTriplet(row: Vector, rowIndex: Long): Array[(Long, (Long, Double))] = {
//    val indexedRow = row.toArray.zipWithIndex
//    indexedRow.map{case (value, colIndex) => (colIndex.toLong, (rowIndex, value))}
//  }
//
//  def buildRow(rowWithIndexes: Iterable[(Long, Double)]): Vector = {
//    val resArr = new Array[Double](rowWithIndexes.size)
//    rowWithIndexes.foreach{case (index, value) =>
//      resArr(index.toInt) = value
//    }
//    Vectors.dense(resArr)
//  }

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("IGA ADI Pregel Solver").master("local[*]").getOrCreate()

    val sc = spark.sparkContext

    // Create a graph with "age" as the vertex property.
    // Here we use a random graph for simplicity.
    val graph: Graph[Array[Long], Int] =
      GraphGenerators.logNormalGraph(sc, numVertices = 12).mapVertices((id, _) => Seq(1, 2, 3).map(_ + id * 100).toArray[Long])

    graph.subgraph(vpred = (v, d) => v > 10)
      .vertices
      .map(_._2)
      .collect
      .foreach(println(_))

//    val r = sc.parallelize(Seq(
//      (1, Array(101d, 102d, 103d)),
//      (2, Array(101d, 102d, 103d))
//    ))
//
//    println(r.reduce((a, b) => (1, Array(2)))._2.mkString(","))



//    // Compute the number of older followers and their total age
//    val olderFollowers: VertexRDD[(Int, Double)] = graph.aggregateMessages[(Int, Double)](
//      triplet => { // Map Function
//        if (triplet.srcAttr > triplet.dstAttr) {
//          // Send message to destination vertex containing counter and age
//          triplet.sendToDst((1, triplet.srcAttr))
//        }
//      },
//      // Add counter and age
//      (a, b) => (a._1 + b._1, a._2 + b._2) // Reduce Function
//    )
//    // Divide total age by number of older followers to get average age of older followers
//    val avgAgeOfOlderFollowers: VertexRDD[Double] =
//      olderFollowers.mapValues((id, value) =>
//        value match {
//          case (count, totalAge) => totalAge / count
//        })
//
//    // Display the results
//    avgAgeOfOlderFollowers.collect.foreach(println(_))
  }

}
