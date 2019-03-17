package edu.agh.kboom.iga.adi.graph.solver

import edu.agh.kboom.iga.adi.graph.solver.core.tree.ProblemTree
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class IgaPartitionerTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

  val assignments = Table(
    ("size", "pc", "src", "dst", "partition"),
    /*
      12 problem tree / 2 partitions
     */
    /*
      partition 0
     */

    // 12
    (12, 2, 8, 4, 0),
    (12, 2, 9, 4, 0),
    (12, 2, 10, 4, 0),
    (12, 2, 11, 5, 0),
    (12, 2, 12, 5, 0),
    (12, 2, 13, 5, 0),

    // 4
    (12, 2, 4, 2, 0),
    (12, 2, 5, 2, 0),

    // 2
    (12, 2, 2, 1, 0),
    (12, 2, 3, 1, 0),

    /*
      partition 1
     */

    // 12
    (12, 2, 14, 6, 1),
    (12, 2, 15, 6, 1),
    (12, 2, 16, 6, 1),
    (12, 2, 17, 7, 1),
    (12, 2, 18, 7, 1),
    (12, 2, 19, 7, 1),

      // 4
    (12, 2, 6, 3, 1),
    (12, 2, 7, 3, 1),

    /*
      768 problem tree / 16 partitions
     */
    // left boundary
    (768, 16, 512, 256, 0),
    (768, 16, 256, 128, 0),
    (768, 16, 128, 64, 0),
    (768, 16, 64, 32, 0),
    (768, 16, 32, 16, 0),
    (768, 16, 16, 8, 0),

    // right boundary
    (768, 16, 542, 271, 0),
    (768, 16, 544, 272, 1),

    // top
    (768, 16, 4, 2, 0),
    (768, 16, 5, 2, 0),
    (768, 16, 6, 3, 1),
    (768, 16, 7, 3, 1),

    // reversed
    (768, 16, 2, 4, 0),
    (768, 16, 2, 5, 0),
    (768, 16, 3, 6, 1),
    (768, 16, 3, 7, 1),
    (768, 16, 271, 542, 0),
    (768, 16, 272, 544, 1)
  )

  forAll(assignments) { (size: Int, pc: Int, src: Int, dst: Int, prt: Int) =>
    val tree = ProblemTree(size)
    val partitioner = IgaPartitioner(tree)

    property(s"$size/$pc: $src->$dst should be assigned $prt") {
      partitioner.getPartition(src, dst, pc) shouldBe prt
    }
  }

}
