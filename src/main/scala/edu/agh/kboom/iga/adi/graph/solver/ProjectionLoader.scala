package edu.agh.kboom.iga.adi.graph.solver

import breeze.linalg.DenseVector
import edu.agh.kboom.iga.adi.graph.solver.core.{Mesh, NoExtractor, SplineSurface, StaticProblem}
import org.apache.spark.SparkContext

object ProjectionLoader {

  def loadSurface(mesh: Mesh, initialProblem: StaticProblem)(implicit sc: SparkContext): SplineSurface = {
    def initialSurface = sc.parallelize(0 until mesh.yDofs)
      .map(y => (y.toLong, DenseVector.tabulate(mesh.xDofs)(x => initialProblem.valueAt(NoExtractor, x, y))))

    SplineSurface(initialSurface, mesh)
  }

}
