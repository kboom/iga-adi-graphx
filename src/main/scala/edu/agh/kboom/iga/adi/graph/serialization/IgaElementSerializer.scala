package edu.agh.kboom.iga.adi.graph.serialization

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{Element, IgaElement, Vertex}

object IgaElementSerializer extends Serializer[IgaElement] {

  def register(kryo: Kryo) {
    kryo.register(classOf[IgaElement], IgaElementSerializer)
  }

  override def write(kryo: Kryo, output: Output, o: IgaElement): Unit = {
    output.writeInt(o.p, true)
    kryo.writeObject(output, o.e)
    kryo.writeObject(output, o.v)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[IgaElement]): IgaElement = {
    val p = input.readInt(true)
    val e = kryo.readObject(input, classOf[Element])
    val v = kryo.readObject(input, classOf[Vertex])
    IgaElement(v, e, p)
  }

}
