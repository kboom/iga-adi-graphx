package edu.agh.kboom.iga.adi.graph.serialization

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import edu.agh.kboom.iga.adi.graph.solver.core.IgaOperation
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core.production.{BackwardsSubstituteBranch, BackwardsSubstituteInterim, BackwardsSubstituteRoot, MergeAndEliminateBranch, MergeAndEliminateInterim, MergeAndEliminateLeaf, MergeAndEliminateRoot, Production}
import edu.agh.kboom.iga.adi.graph.solver.core.tree._

object IgaSerializers {
  def register(kryo: Kryo) {
//    kryo.register(classOf[IgaOperation], IgaOperationSerializer)
//    kryo.register(classOf[Array[IgaOperation]])
//    kryo.register(classOf[Production])
//    kryo.register(classOf[Production], ProductionSerializer)
//    kryo.register(MergeAndEliminateRoot.getClass, ProductionSerializer)
//    kryo.register(MergeAndEliminateInterim.getClass, ProductionSerializer)
//    kryo.register(MergeAndEliminateBranch.getClass, ProductionSerializer)
//    kryo.register(MergeAndEliminateLeaf.getClass, ProductionSerializer)
//    kryo.register(BackwardsSubstituteRoot.getClass, ProductionSerializer)
//    kryo.register(BackwardsSubstituteBranch.getClass, ProductionSerializer)
//    kryo.register(BackwardsSubstituteInterim.getClass, ProductionSerializer)
  }
}

object IgaOperationSerializer extends Serializer[IgaOperation] {

  override def write(kryo: Kryo, output: Output, o: IgaOperation): Unit = {
    kryo.writeObject(output, o.src)
    kryo.writeObject(output, o.dst)
    kryo.writeObject(output, o.p)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[IgaOperation]): IgaOperation = {
    val src = kryo.readObject(input, classOf[Vertex])
    val dst = kryo.readObject(input, classOf[Vertex])
    val p = kryo.readObject(input, classOf[Production])
    IgaOperation(src, dst, p)
  }

}





//object ProductionSerializer extends Serializer[Production] {
//
//  private val RootVertexType = 0
//  private val InterimVertexType = 1
//  private val BranchVertexType = 2
//  private val LeafVertexType = 3
//
//  override def write(kryo: Kryo, output: Output, src: Production): Unit = {
//    src match {
//      case RootVertex() => output.writeShort(RootVertexType)
//      case InterimVertex(_) => output.writeShort(InterimVertexType)
//      case BranchVertex(_) => output.writeShort(BranchVertexType)
//      case LeafVertex(_) => output.writeShort(LeafVertexType)
//    }
//    output.writeLong(src.id, true)
//  }
//
//  override def read(kryo: Kryo, input: Input, `type`: Class[Production]): Production = {
//    val t = input.readShort()
//    val id = input.readLong(true)
//    t match {
//      case RootVertexType => RootVertex()
//      case InterimVertexType => InterimVertex(id)
//      case BranchVertexType => BranchVertex(id)
//      case LeafVertexType => LeafVertex(id)
//    }
//  }
//
//}