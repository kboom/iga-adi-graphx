package edu.agh.kboom

case class Node(
                 start: Double = 0,
                 end: Double = 1,
                 mA: Array[Array[Double]] = Array.ofDim[Double](7, 7),
                 mB: Array[Array[Double]] = Array.ofDim[Double](7, 7),
                 mX: Array[Array[Double]] = Array.ofDim[Double](7, 7)
               )

object Node {

}
