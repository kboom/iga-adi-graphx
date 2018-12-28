package edu.agh.kboom.iga.adi.graph.solver.core

import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree.lastIndexOfLeafRow
import edu.agh.kboom.iga.adi.graph.solver.core.tree.Vertex.{childIndicesOf, vertexOf}
import edu.agh.kboom.iga.adi.graph.solver.core.tree.{ProblemTree, Vertex}

object IgaTasks {

  def generateOperations(pt: ProblemTree): Seq[IgaOperation] =
    (1 to lastIndexOfLeafRow(pt)).flatMap(
      idx => childIndicesOf(vertexOf(idx)(pt))(pt).flatMap(
        v1 => Some(Vertex.vertexOf(idx)(pt)).map(v2 => Seq(
          IgaOperation.operationFor(v1, v2),
          IgaOperation.operationFor(v2, v1)
        )).map(_.flatten)
      ).flatten
    )

}
