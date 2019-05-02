package edu.agh.kboom.iga.adi.graph.serialization

import breeze.linalg.{DenseMatrix, DenseVector}
import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixA.MatrixA
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixB.MatrixB
import edu.agh.kboom.iga.adi.graph.solver.core.MatrixX.MatrixX

object BreezeSerializers {
  def register(kryo: Kryo) {
    val loader = getClass.getClassLoader

    kryo.register(classOf[MatrixA], DenseMatrixSerializer)
    kryo.register(classOf[MatrixB], DenseMatrixSerializer)
    kryo.register(classOf[MatrixX], DenseMatrixSerializer)
    kryo.register(classOf[DenseMatrix[Double]], DenseMatrixSerializer)
    kryo.register(classOf[DenseMatrix[Double]], DenseMatrixSerializer)
    kryo.register(classOf[DenseVector[Double]], DenseVectorSerializer)
    kryo.register(Class.forName("breeze.linalg.DenseMatrix$mcD$sp", false, loader), DenseMatrixSerializer)
    kryo.register(Class.forName("breeze.linalg.DenseVector$mcD$sp", false, loader), DenseVectorSerializer)
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
    val content = input.readDoubles(size)
    DenseMatrix.create(rows, cols, content)
  }

}

object DenseVectorSerializer extends Serializer[DenseVector[Double]] {

  override def write(kryo: Kryo, output: Output, src: DenseVector[Double]): Unit = {
    output.writeInt(src.size, true)
    output.writeDoubles(src.data)
  }

  override def read(kryo: Kryo, input: Input, `type`: Class[DenseVector[Double]]): DenseVector[Double] = {
    val size = input.readInt(true)
    DenseVector.create(input.readDoubles(size), 0, 0, size)
  }

}

