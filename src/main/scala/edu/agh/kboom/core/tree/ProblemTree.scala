package edu.agh.kboom.core.tree

import breeze.numerics.{log2, pow}

case class ProblemTree(size: Int)

object ProblemTree {

  def leafHeight(implicit tree: ProblemTree): Int = branchingHeight(tree) + 1

  def branchingHeight(implicit tree: ProblemTree): Int = (log2(tree.size / 3.0) + 1).toInt

  def firstIndexOfRow(level: Int): Int = pow(2, level - 1)

  def firstIndexOfLeafRow(implicit tree: ProblemTree): Int = firstIndexOfRow(leafHeight(tree))

  def lastIndexOfLeafRow(implicit tree: ProblemTree): Int = firstIndexOfLeafRow + strengthOfRow(leafHeight) - 1

  def firstIndexOfBranchingRow(implicit tree: ProblemTree): Int = firstIndexOfRow(branchingHeight(tree))

  def lastIndexOfBranchingRow(implicit tree: ProblemTree): Int = firstIndexOfBranchingRow + strengthOfRow(branchingHeight(tree)) - 1

  def strengthOfRow(level: Int)(implicit tree: ProblemTree): Int = if (level < leafHeight) pow(2, level - 1) else 3 * pow(2, level - 2)

}
