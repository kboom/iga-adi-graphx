package edu.agh.kboom.iga.adi.graph.spark

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}

import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.serializer.JavaSerializer

class EvenlyDistributedRDD(@transient sc: SparkContext, first: Long, last: Long) extends RDD[(Long, Long)](sc, Nil) {

  val locations = Option(System.getenv("WORKER_HOSTNAMES")).map(_.split(","))
    .getOrElse(Array("local[0]"))

  override def getPartitions: Array[Partition] = {
    val total = last - first
    val partitionCount = sc.defaultParallelism
    val segmentSize = Math.floor(total / partitionCount).toLong
    (0.until(partitionCount - 1).map(p => new EvenlyDistributedRangePartition(id, p, first + p * segmentSize, first + (p + 1) * segmentSize - 1)) ++
      Seq(new EvenlyDistributedRangePartition(id, partitionCount - 1, first + (partitionCount - 1) * segmentSize, first + partitionCount * segmentSize + (total % partitionCount)))).toArray
  }

  override def compute(s: Partition, context: TaskContext): Iterator[(Long, Long)] =
    new InterruptibleIterator(context, s.asInstanceOf[EvenlyDistributedRangePartition].iterator)

  override def getPreferredLocations(s: Partition): Seq[String] = {
//    Seq(locations(s.index % locations.length))
    Nil
  }

}

class EvenlyDistributedRangePartition(var rddId: Long, var slice: Int, var start: Long, var end: Long)
  extends Partition with Serializable {

  def iterator: Iterator[(Long, Long)] = (start to end).map((_, 0L)).iterator

  override def index: Int = slice

  override def hashCode(): Int = (41 * (41 + rddId) + slice).toInt

  override def equals(other: Any): Boolean = other match {
    case that: EvenlyDistributedRangePartition =>
      this.rddId == that.rddId && this.slice == that.slice
    case _ => false
  }

  @throws(classOf[IOException])
  private def writeObject(out: ObjectOutputStream): Unit = {
    val sfactory = SparkEnv.get.serializer

    sfactory match {
      case js: JavaSerializer => out.defaultWriteObject()
      case _ =>
        out.writeLong(rddId)
        out.writeInt(slice)
        out.writeLong(start)
        out.writeLong(end)
    }
  }

  @throws(classOf[IOException])
  private def readObject(in: ObjectInputStream): Unit = {

    val sfactory = SparkEnv.get.serializer
    sfactory match {
      case js: JavaSerializer => in.defaultReadObject()
      case _ =>
        rddId = in.readLong()
        slice = in.readInt()
        start = in.readLong()
        end = in.readLong()
    }
  }

}
