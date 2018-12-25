package edu.agh.kboom.core.initialisation

import edu.agh.kboom.core.IgaContext
import edu.agh.kboom.core.tree.Element
import org.apache.spark.SparkContext
import org.apache.spark.graphx.VertexId
import org.apache.spark.rdd.RDD

trait LeafInitializer {

  def leafData(ctx: IgaContext)(implicit sc: SparkContext): RDD[(VertexId, Element)]

}
