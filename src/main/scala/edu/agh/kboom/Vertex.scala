package edu.agh.kboom

import breeze.numerics.{floor, log2, pow}
import edu.agh.kboom.ProblemTree._

sealed abstract class Vertex {
  def id: Int
}

case class InterimVertex(id: Int) extends Vertex

case class BranchVertex(id: Int) extends Vertex

case class LeafVertex(id: Int) extends Vertex

object Vertex {

  def vertexOf(id: Int)(implicit tree: ProblemTree): Vertex = id match {
    case _ if id < firstIndexOfBranchingRow(tree) => InterimVertex(id)
    case _ if id < firstIndexOfLeafRow(tree) => BranchVertex(id)
    case _ => LeafVertex(id)
  }

  def rowIndexOf(v: Vertex)(implicit tree: ProblemTree): Int = v match {
    case LeafVertex(_) => leafHeight(tree)
    case BranchVertex(_) => branchingHeight(tree)
    case InterimVertex(_) => floor(log2(v.id)).toInt + 1
  }

  def onTopOfBranchingRow(v: Vertex)(implicit tree: ProblemTree): Boolean = v match {
    case InterimVertex(_) => rowIndexOf(v) == branchingHeight(tree) - 1
    case _ => false
  }

  def strengthOf(v: Vertex)(implicit tree: ProblemTree): Int = strengthOfRow(rowIndexOf(v))

  def offsetLeft(v: Vertex)(implicit tree: ProblemTree): Int = v.id - firstIndexOfRow(rowIndexOf(v))

  def leftChildOf(v: Vertex)(implicit tree: ProblemTree): Option[Vertex] = v match {
    case InterimVertex(id) => if (onTopOfBranchingRow(v)) Some(BranchVertex(2 * id)) else Some(InterimVertex(2 * id))
    case BranchVertex(id) => Some(LeafVertex(firstIndexOfLeafRow(tree) + 3 * (id - firstIndexOfBranchingRow(tree))))
    case LeafVertex(_) => None
  }

  def childIndicesOf(v: Vertex)(implicit tree: ProblemTree): Seq[Vertex] = leftChildOf(v) match {
    case Some(InterimVertex(id)) => Seq(id, id + 1).map(InterimVertex)
    case Some(BranchVertex(id)) => Seq(id, id + 1).map(BranchVertex)
    case Some(LeafVertex(id)) => Seq(id, id + 1, id + 2).map(LeafVertex)
    case None => Nil
  }

  def inRegularArea(idx: Int)(implicit tree: ProblemTree): Boolean = idx < firstIndexOfBranchingRow(tree)

  def segmentOf(v: Vertex)(implicit tree: ProblemTree): (Double, Double) =
    Some(tree.size / strengthOf(v).toDouble).map(share => (share * offsetLeft(v), share * (offsetLeft(v) + 1))).get

}
