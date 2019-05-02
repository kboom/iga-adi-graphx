package edu.agh.kboom.iga.adi.graph.serialization

import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.esotericsoftware.kryo.io.{Input, Output}
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Element

object ElementSerializer extends Serializer[Element] {

  def register(kryo: Kryo) {
    kryo.register(classOf[Element], ElementSerializer)
  }

  override def write(kryo: Kryo, output: Output, o: Element): Unit = {
    output.writeInt(o.elements, true)
    kryo.writeObject(output, o.mA)
    kryo.writeObject(output, o.mB)
    kryo.writeObject(output, o.mX)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[Element]): Element = {
    val elements = input.readInt(true)
    val mA = kryo.readObject(input, classOf[MatrixA])
    val mB = kryo.readObject(input, classOf[MatrixB])
    val mX = kryo.readObject(input, classOf[MatrixX])
    Element(elements, mA, mB, mX)
  }

}
