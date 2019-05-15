package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class VertexPartitionerTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

  val assignments = Table(
    ("size", "part", "v", "partition"),
    (24576, 80, 16384, 0),
    (24576, 80, 16385, 0),
    (24576, 80, 16692, 1),
    (24576, 80, 28672, 39),
    (24576, 80, 40959, 79),
    (24576, 80, 40960, 79)
  )

  forAll(assignments) { (size: Int, npart: Int, v: Int, prt: Int) =>
    val tree = ProblemTree(size)
    val partitioner = VertexPartitioner(npart, tree)

    property(s"$v should be assigned $prt") {
      partitioner.getPartition(v.toLong) shouldBe prt
    }
  }

}
