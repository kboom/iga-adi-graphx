import org.apache.spark.sql.{Encoder, Encoders, SparkSession}

final case class Dummy(number: Integer,
                       word: String)

object HelloSpark {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("hello spark").master("spark://localhost:32769").getOrCreate()

    implicit val dummyEncoder: Encoder[Dummy] = Encoders.kryo[Dummy]
    implicit val stringEncoder: Encoder[String] = Encoders.kryo[String]


    val data = Seq(
      Dummy(1, "hello"),
      Dummy(2, "hello"),
      Dummy(3, "hello"),
      Dummy(4, "bye"),
      Dummy(5, "bye"),
      Dummy(6, "bye")
    )

    val df1 = spark.createDataset(data).as[Dummy]

    df1.printSchema()

//    println(df1.groupByKey(_.word).reduceGroups().collect())

//    println("count: ")
//    println(df.count())

    spark.stop()
  }
}
