package edu.agh.kboom.iga.adi.graph.serialization

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX
import edu.agh.kboom.iga.adi.graph.solver.core.production._

object ProductionMessageSerializer extends Serializer[ProductionMessage] {

  private val MergeAndEliminateBranchMessageType = 0
  private val MergeAndEliminateInterimMessageType = 1
  private val MergeAndEliminateRootMessageType = 2
  private val MergeAndEliminateLeafMessageType = 3
  private val BackwardsSubstituteInterimMessageType = 4
  private val BackwardsSubstituteRootMessageType = 5
  private val BackwardsSubstituteBranchMessageType = 6

  def register(kryo: Kryo) {
    kryo.register(classOf[ProductionMessage], ProductionMessageSerializer)
    kryo.register(classOf[MergeAndEliminateInterimMessage], ProductionMessageSerializer)
    kryo.register(classOf[MergeAndEliminateRootMessage], ProductionMessageSerializer)
    kryo.register(classOf[MergeAndEliminateLeafMessage], ProductionMessageSerializer)
    kryo.register(classOf[MergeAndEliminateBranchMessage], ProductionMessageSerializer)
    kryo.register(classOf[BackwardsSubstituteInterimMessage], ProductionMessageSerializer)
    kryo.register(classOf[BackwardsSubstituteRootMessage], ProductionMessageSerializer)
    kryo.register(classOf[BackwardsSubstituteBranchMessage], ProductionMessageSerializer)
    kryo.register(classOf[Array[ProductionMessage]])
  }

  override def write(kryo: Kryo, output: Output, src: ProductionMessage): Unit = {
    src match {
      case MergeAndEliminateInterimMessage(a, b) =>
        output.writeShort(MergeAndEliminateInterimMessageType)
        kryo.writeObject(output, a)
        kryo.writeObject(output, b)

      case MergeAndEliminateRootMessage(a, b) =>
        output.writeShort(MergeAndEliminateRootMessageType)
        kryo.writeObject(output, a)
        kryo.writeObject(output, b)

      case MergeAndEliminateLeafMessage(a, b) =>
        output.writeShort(MergeAndEliminateLeafMessageType)
        kryo.writeObject(output, a)
        kryo.writeObject(output, b)

      case MergeAndEliminateBranchMessage(a, b) =>
        output.writeShort(MergeAndEliminateBranchMessageType)
        kryo.writeObject(output, a)
        kryo.writeObject(output, b)

      case BackwardsSubstituteInterimMessage(a) =>
        output.writeShort(BackwardsSubstituteInterimMessageType)
        kryo.writeObject(output, a)

      case BackwardsSubstituteRootMessage(a) =>
        output.writeShort(BackwardsSubstituteRootMessageType)
        kryo.writeObject(output, a)

      case BackwardsSubstituteBranchMessage(a) =>
        output.writeShort(BackwardsSubstituteBranchMessageType)
        kryo.writeObject(output, a)
    }
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[ProductionMessage]): ProductionMessage = {
    val t = input.readShort()
    t match {
      case MergeAndEliminateInterimMessageType =>
        MergeAndEliminateInterimMessage(
          kryo.readObject(input, classOf[MatrixA]),
          kryo.readObject(input, classOf[MatrixB])
        )

      case MergeAndEliminateRootMessageType =>
        MergeAndEliminateRootMessage(
          kryo.readObject(input, classOf[MatrixA]),
          kryo.readObject(input, classOf[MatrixB])
        )

      case MergeAndEliminateLeafMessageType =>
        MergeAndEliminateLeafMessage(
          kryo.readObject(input, classOf[MatrixA]),
          kryo.readObject(input, classOf[MatrixB])
        )

      case MergeAndEliminateBranchMessageType =>
        MergeAndEliminateBranchMessage(
          kryo.readObject(input, classOf[MatrixA]),
          kryo.readObject(input, classOf[MatrixB])
        )

      case BackwardsSubstituteInterimMessageType =>
        BackwardsSubstituteInterimMessage(
          kryo.readObject(input, classOf[MatrixX])
        )

      case BackwardsSubstituteRootMessageType =>
        BackwardsSubstituteRootMessage(
          kryo.readObject(input, classOf[MatrixX])
        )

      case BackwardsSubstituteBranchMessageType =>
        BackwardsSubstituteBranchMessage(
          kryo.readObject(input, classOf[MatrixX])
        )
    }
  }

}
