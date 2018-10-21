import org.apache.spark.sql.{Encoder, Encoders, SparkSession}


object BasicRDDExample {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("basic rdd example").master("local[*]").getOrCreate()

    val input = spark.sparkContext.parallelize(1 to 1000)
    println(input.reduce(_ + _))

    val words = spark.sparkContext.parallelize(Seq("hello", "world", "hello", "hello"))
    println(words.map(s => (s, 1)).reduceByKey(_ + _).collect().mkString(","))

    spark.stop()
  }

}
