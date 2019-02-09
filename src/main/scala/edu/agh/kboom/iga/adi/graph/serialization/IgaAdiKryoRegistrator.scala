package edu.agh.kboom.iga.adi.graph.serialization

import breeze.linalg.DenseMatrix
import com.esotericsoftware.kryo.Kryo
import edu.agh.kboom.iga.adi.graph.solver.core.IgaOperation
import edu.agh.kboom.iga.adi.graph.solver.core.production._
import edu.agh.kboom.iga.adi.graph.solver.core.tree._
import org.apache.spark.graphx.Edge
import org.apache.spark.serializer.KryoRegistrator

class IgaAdiKryoRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) = {
    Array(
      classOf[DenseMatrix[Double]],
      classOf[IgaElement],
      classOf[Element],
      classOf[IgaOperation],
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
