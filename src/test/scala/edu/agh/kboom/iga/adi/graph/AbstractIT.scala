package edu.agh.kboom.iga.adi.graph

import java.nio.file.Files

import edu.agh.kboom.SubjectSpec
import org.apache.spark.sql.SparkSession
import org.scalatest._

trait SparkSession extends SuiteMixin with BeforeAndAfterAll { this: TestSuite =>

  private val spark = SparkSession.builder
    .appName("IGA ADI Pregel Solver")
    .master("local[*]")
    .getOrCreate()

  implicit val sc = spark.sparkContext

  sc.setCheckpointDir(Files.createTempDirectory("spark").toAbsolutePath.toString)

  override protected def afterAll(): Unit = spark.stop()

}

abstract class AbstractIT extends SubjectSpec with SparkSession
