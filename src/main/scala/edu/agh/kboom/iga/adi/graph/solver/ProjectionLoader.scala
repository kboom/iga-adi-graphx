package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.core.{Mesh, NoExtractor, SplineSurface, StaticProblem}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}

object ProjectionLoader {

  def loadSurface(mesh: Mesh, initialProblem: StaticProblem)(implicit sc: SparkContext): SplineSurface = {
    def initialSurface = new IndexedRowMatrix(sc.parallelize(0 until mesh.yDofs)
      .map(y => IndexedRow(y, Vectors.dense((0 until mesh.xDofs).map(x => initialProblem.valueAt(NoExtractor, x, y)).toArray))))

    SplineSurface(initialSurface, mesh)
  }

}
