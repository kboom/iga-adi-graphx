package edu.agh.kboom.iga.adi.graph.serialization

import com.esotericsoftware.kryo.Kryo
import edu.agh.kboom.iga.adi.graph.solver.core.production._
import org.apache.spark.graphx.Edge
import org.apache.spark.serializer.KryoRegistrator

class IgaAdiKryoRegistrator extends KryoRegistrator {

  override def registerClasses(kryo: Kryo): Unit = {
    val loader = getClass.getClassLoader
    Array(
      Class.forName("scala.reflect.ClassTag$$anon$1", false, loader),
      Class.forName("scala.reflect.ManifestFactory$$anon$10", false, loader),
      Class.forName("scala.reflect.ManifestFactory$$anon$9", false, loader),
      Class.forName("org.apache.spark.graphx.util.collection.GraphXPrimitiveKeyOpenHashMap$mcJI$sp", false, loader),
      Class.forName("org.apache.spark.graphx.util.collection.GraphXPrimitiveKeyOpenHashMap$$anonfun$1", false, loader),
      Class.forName("org.apache.spark.graphx.util.collection.GraphXPrimitiveKeyOpenHashMap$$anonfun$2", false, loader),
      Class.forName("org.apache.spark.util.collection.OpenHashSet$LongHasher", false, loader),
      Class.forName("org.apache.spark.graphx.impl.ShippableVertexPartition", false, loader),
      Class.forName("org.apache.spark.graphx.impl.RoutingTablePartition", false, loader),
      MergeAndEliminateRoot.getClass,
      MergeAndEliminateInterim.getClass,
      MergeAndEliminateBranch.getClass,
      MergeAndEliminateLeaf.getClass,
      BackwardsSubstituteRoot.getClass,
      BackwardsSubstituteBranch.getClass,
      BackwardsSubstituteInterim.getClass,
      classOf[scala.collection.mutable.WrappedArray.ofRef[_]],
      classOf[java.lang.Class[_]]
    ).foreach(kryo.register)

    OptionSerializers.register(kryo)
    BreezeSerializers.register(kryo)
    VertexSerializer.register(kryo)
    ElementSerializer.register(kryo)
    IgaElementSerializer.register(kryo)
    IgaOperationSerializer.register(kryo)
    IgaSerializers.register(kryo)
    ProductionMessageSerializer.register(kryo)
  }
}
