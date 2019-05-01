package edu.agh.kboom.iga.adi.graph.serialization

import breeze.linalg.DenseMatrix
import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX

object BreezeSerializers {
  def register(kryo: Kryo) {
    kryo.register(classOf[MatrixA], DenseMatrixSerializer)
    kryo.register(classOf[MatrixB], DenseMatrixSerializer)
    kryo.register(classOf[MatrixX], DenseMatrixSerializer)
    kryo.register(classOf[DenseMatrix[Double]], DenseMatrixSerializer)
  }
}

object DenseMatrixSerializer extends Serializer[DenseMatrix[Double]] {

  override def write(kryo: Kryo, output: Output, src: DenseMatrix[Double]): Unit = {
    output.writeInt(src.rows, true)
    output.writeInt(src.cols, true)
    output.writeInt(src.size, true)
    output.writeDoubles(src.data)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[DenseMatrix[Double]]): DenseMatrix[Double] = {
    val rows = input.readInt(true)
    val cols = input.readInt(true)
    val size = input.readInt(true)
    DenseMatrix.create(rows, rows, input.readDoubles(size))
  }

}

