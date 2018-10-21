import org.apache.spark.sql.{Encoder, Encoders, SparkSession}

sealed case class Animal(
                   name: String,
                   legs: Int
                 )

object BasicDatasetExample {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("basic dataset example").master("local[*]").getOrCreate()

    import spark.implicits._

    implicit val dummyEncoder: Encoder[Dummy] = Encoders.kryo[Dummy]

    val ds = Seq(Animal("Duck", 2), Animal("Dog", 4)).toDS()

    spark.stop()
  }

}
