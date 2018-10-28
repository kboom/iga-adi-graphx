package edu.agh.kboom

case class Mesh(xSize: Int, ySize: Int, xRes: Int, yRes: Int) {
  val xDofs: Int = xSize + Mesh.SPLINE_ORDER
  val yDofs: Int = ySize + Mesh.SPLINE_ORDER
  val dx: Int = xRes / xSize
  val dy: Int = yRes / ySize
}

object Mesh {

  val SPLINE_ORDER = 2

}
