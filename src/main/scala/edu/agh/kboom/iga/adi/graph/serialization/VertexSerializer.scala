package edu.agh.kboom.iga.adi.graph.serialization

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import edu.agh.kboom.iga.adi.graph.solver.core.tree._

object VertexSerializer extends Serializer[Vertex] {

  private val RootVertexType = 0
  private val InterimVertexType = 1
  private val BranchVertexType = 2
  private val LeafVertexType = 3

  def register(kryo: Kryo) {
    kryo.register(classOf[Vertex], VertexSerializer)
    kryo.register(classOf[RootVertex], VertexSerializer)
    kryo.register(classOf[InterimVertex], VertexSerializer)
    kryo.register(classOf[BranchVertex], VertexSerializer)
    kryo.register(classOf[LeafVertex], VertexSerializer)
  }

  override def write(kryo: Kryo, output: Output, src: Vertex): Unit = {
    src match {
      case RootVertex() => output.writeShort(RootVertexType)
      case InterimVertex(_) => output.writeShort(InterimVertexType)
      case BranchVertex(_) => output.writeShort(BranchVertexType)
      case LeafVertex(_) => output.writeShort(LeafVertexType)
    }
    output.writeLong(src.id, true)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[Vertex]): Vertex = {
    val t = input.readShort()
    val id = input.readLong(true)
    t match {
      case RootVertexType => RootVertex()
      case InterimVertexType => InterimVertex(id)
      case BranchVertexType => BranchVertex(id)
      case LeafVertexType => LeafVertex(id)
    }
  }

}
