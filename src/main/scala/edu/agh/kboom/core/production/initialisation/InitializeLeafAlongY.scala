package edu.agh.kboom.core.production.initialisation

import edu.agh.kboom.core.production.{Production, ProductionMessage}
import edu.agh.kboom.core.tree.{IgaElement, MethodCoefficients, ProblemTree}
import edu.agh.kboom.core.{IgaTaskContext, Mesh, Partition, Solution}

sealed case class InitializeLeafAlongYMessage(p: InitializeLeafAlongY) extends ProductionMessage {
  override val production: Production = p
}

case class InitializeLeafAlongY(hsi: Solution) extends Production {

  def initialize(e: IgaElement)(implicit ctx: IgaTaskContext): Unit = {
    MethodCoefficients.bind(e.mA)
    initializeRightHandSides(e)
  }

  private def initializeRightHandSides(e: IgaElement)(implicit ctx: IgaTaskContext): Unit = {
    implicit val problemTree: ProblemTree = ctx.mc.xTree()
    implicit val mesh: Mesh = ctx.mc.mesh

    val partition = findPartition(e)

    val rows = hsi.m.rows.filter(ir => Seq(0, 1, 2).map(_ + partition.idx).contains(ir.index)).collect()

    for (i <- 0 until mesh.yDofs) {
      e.mB.replace(0, i, partition.left * rows(0).vector(i))
      e.mB.replace(1, i, partition.left * rows(1).vector(i))
      e.mB.replace(2, i, partition.left * rows(2).vector(i))
    }
  }

  def findPartition(e: IgaElement) = Partition(0,0,0,0)

}
