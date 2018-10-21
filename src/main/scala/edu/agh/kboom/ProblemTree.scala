package edu.agh.kboom

import breeze.numerics.{floor, log2, pow}

case class ProblemTree(size: Int)

object ProblemTree {

  def totalHeightOf(tree: ProblemTree): Int = regularHeightOf(tree) + 1

  def regularHeightOf(tree: ProblemTree): Int = (log2(tree.size / 3.0) + 1).toInt

  def firstIndexOfLastRow(tree: ProblemTree): Int = pow(2, totalHeightOf(tree) - 1)

  def lastIndexOfLastRow(tree: ProblemTree): Int = firstIndexOfLastRow(tree) + tree.size - 1

  def firstIndexOfPenultimateRow(tree: ProblemTree): Int = pow(2, regularHeightOf(tree) - 1)

  def lastIndexOfPenultimateRow(tree: ProblemTree): Int = pow(2, regularHeightOf(tree)) - 1

  def leftChildOf(parentIdx: Int)(implicit tree: ProblemTree): Option[Int] = parentIdx match {
    case x if x < firstIndexOfPenultimateRow(tree) => Some(2 * x)
    case x if x <= lastIndexOfPenultimateRow(tree) => Some(firstIndexOfLastRow(tree) + 3 * (x - firstIndexOfPenultimateRow(tree)))
    case _ => None
  }

  def childIndicesOf(parentIdx: Int)(implicit tree: ProblemTree): Seq[Int] = parentIdx match {
    case x if x < firstIndexOfPenultimateRow(tree) => leftChildOf(x).map(i => Seq(i, i + 1)).getOrElse(Nil)
    case x if x <= lastIndexOfPenultimateRow(tree) => leftChildOf(x).map(i => Seq(i, i + 1, i + 2)).getOrElse(Nil)
    case _ => Nil
  }

}
