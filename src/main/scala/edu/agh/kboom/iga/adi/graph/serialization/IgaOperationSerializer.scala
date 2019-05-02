package edu.agh.kboom.iga.adi.graph.serialization

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import edu.agh.kboom.iga.adi.graph.solver.core.IgaOperation
import edu.agh.kboom.iga.adi.graph.solver.core.production.Production
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex

object IgaOperationSerializer extends Serializer[IgaOperation] {

  def register(kryo: Kryo) {
    kryo.register(classOf[IgaOperation], IgaOperationSerializer)
    kryo.register(classOf[Array[IgaOperation]])
  }

  override def write(kryo: Kryo, output: Output, o: IgaOperation): Unit = {
    kryo.writeObject(output, o.src)
    kryo.writeObject(output, o.dst)
    kryo.writeClassAndObject(output, o.p)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[IgaOperation]): IgaOperation = {
    val src = kryo.readObject(input, classOf[Vertex])
    val dst = kryo.readObject(input, classOf[Vertex])
    val p = kryo.readClassAndObject(input)
    IgaOperation(src, dst, p.asInstanceOf[Production])
  }

}
