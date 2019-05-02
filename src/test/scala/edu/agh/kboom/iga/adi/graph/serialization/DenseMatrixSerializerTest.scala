package edu.agh.kboom.iga.adi.graph.serialization

import breeze.linalg.DenseMatrix
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import org.scalatest.{FlatSpec, Matchers}

class DenseMatrixSerializerTest extends FlatSpec with Matchers {

    "serialization" should "work" in {
      val kryo = new Kryo()
      BreezeSerializers.register(kryo)

      val mw = DenseMatrix.create(2, 3, Array(
        1d, 2d, 3d,
        4d, 5d, 6d
      ))

      val output = new Output(1024, -1)
      kryo.writeObject(output, mw)

      val input = new Input(output.getBuffer, 0, output.position)
      val mr = kryo.readObject(input, classOf[DenseMatrix[Double]])

      mw shouldBe mr
    }

}
