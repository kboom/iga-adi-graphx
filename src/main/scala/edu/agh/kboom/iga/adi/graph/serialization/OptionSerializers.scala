package edu.agh.kboom.iga.adi.graph.serialization

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}

object OptionSerializers {
  def register(kryo: Kryo) {
    kryo.register(Class.forName("scala.None$"), new NoneSerializer())
    kryo.register(classOf[Array[None.type]], new NoneArraySerializer())
    kryo.register(classOf[Some[_]], new SomeSerializer(kryo))
  }
}

class NoneSerializer extends Serializer[None.type] {
  override def write(kryo: Kryo, output: Output, `object`: None.type): Unit = ()

  override def read(kryo: Kryo, input: Input, `type`: Class[None.type]): None.type = None
}

class NoneArraySerializer extends Serializer[Array[None.type]] {
  override def write(kryo: Kryo, output: Output, `object`: Array[None.type]): Unit = {
    output.writeVarInt(`object`.length, true)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[Array[None.type]]): Array[None.type] = Array.fill(input.readVarInt(true))(None)
}

class SomeSerializer(kryo: Kryo) extends Serializer[Some[_]] {
  override def write(kryo: Kryo, output: Output, `object`: Some[_]): Unit = kryo.writeClassAndObject(output, `object`)

  override def read(kryo: Kryo, input: Input, `type`: Class[Some[_]]): Some[_] = Some(kryo.readClassAndObject(input))
}