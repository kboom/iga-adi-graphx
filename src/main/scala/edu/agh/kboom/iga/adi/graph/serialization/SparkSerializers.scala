package edu.agh.kboom.iga.adi.graph.serialization

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import edu.agh.kboom.iga.adi.graph.solver.core.IgaOperation
import org.apache.spark.graphx.Edge

object SparkSerializers extends Serializer[Edge[IgaOperation]] {

  def register(kryo: Kryo) {
    kryo.register(classOf[Edge[IgaOperation]], SparkSerializers)
    kryo.register(classOf[Array[Edge[IgaOperation]]])
  }

  override def write(kryo: Kryo, output: Output, o: Edge[IgaOperation]): Unit = {
    output.writeLong(o.srcId, true)
    output.writeLong(o.dstId, true)
    kryo.writeObject(output, o.attr)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[Edge[IgaOperation]]): Edge[IgaOperation] = {
    val src = input.readLong(true)
    val dst =  input.readLong(true)
    val attr = kryo.readObject(input, classOf[IgaOperation])
    Edge(src, dst, attr)
  }

}
