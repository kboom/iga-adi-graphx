package edu.agh.kboom.iga.adi.graph.serialization

import breeze.linalg.{DenseMatrix, DenseVector}
import com.esotericsoftware.kryo.Kryo
import edu.agh.kboom.iga.adi.graph.solver.core.IgaOperation
import edu.agh.kboom.iga.adi.graph.solver.core.production._
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import org.apache.spark.graphx.Edge
import org.apache.spark.serializer.KryoRegistrator

class IgaAdiKryoRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) = {
    val loader = getClass().getClassLoader()
    Array(
      Class.forName("scala.reflect.ClassTag$$anon$1", false, loader),
      Class.forName("breeze.linalg.DenseMatrix$mcD$sp", false, loader),
      Class.forName("scala.reflect.ManifestFactory$$anon$10", false, loader),
      Class.forName("scala.reflect.ManifestFactory$$anon$9", false, loader),
      Class.forName("org.apache.spark.graphx.util.collection.GraphXPrimitiveKeyOpenHashMap$mcJI$sp", false, loader),
      Class.forName("org.apache.spark.graphx.util.collection.GraphXPrimitiveKeyOpenHashMap$$anonfun$1", false, loader),
      Class.forName("org.apache.spark.graphx.util.collection.GraphXPrimitiveKeyOpenHashMap$$anonfun$2", false, loader),
      Class.forName("org.apache.spark.util.collection.OpenHashSet$LongHasher", false, loader),
      classOf[DenseMatrix[Double]],
      classOf[DenseVector[Double]],
      classOf[IgaElement],
      classOf[Element],
      classOf[IgaOperation],
      classOf[Array[IgaOperation]],
      classOf[RootVertex],
      classOf[LeafVertex],
      classOf[BranchVertex],
      classOf[InterimVertex],
      classOf[Production],
      classOf[MergeAndEliminateInterimMessage],
      classOf[MergeAndEliminateRootMessage],
      classOf[MergeAndEliminateLeafMessage],
      classOf[MergeAndEliminateBranchMessage],
      classOf[BackwardsSubstituteInterimMessage],
      classOf[BackwardsSubstituteRootMessage],
      classOf[BackwardsSubstituteBranchMessage],
      MergeAndEliminateRoot.getClass,
      MergeAndEliminateInterim.getClass,
      MergeAndEliminateBranch.getClass,
      MergeAndEliminateLeaf.getClass,
      BackwardsSubstituteRoot.getClass,
      BackwardsSubstituteBranch.getClass,
      BackwardsSubstituteInterim.getClass,
      classOf[Edge[_]],
      classOf[Array[Edge[_]]],
      classOf[Array[IgaElement]],
      classOf[Array[Array[Double]]]
    ).foreach(kryo.register)

    //    kryo.register(classOf[MergeAndEliminateRoot], new HelloSerializer())
  }
}
