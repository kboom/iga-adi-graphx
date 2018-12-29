package edu.agh.kboom.iga.adi.graph.solver.core

abstract class Spline(lb: Int, ub: Int) {
  final def getValue(x: Double): Double = if (belongsToDomain(x)) functionValueAt(x) else Spline.VALUE_OUTSIDE_DOMAIN

  protected def functionValueAt(x: Double): Double

  def firstDerivativeValueAt(x: Double): Double

  def secondDerivativeValueAt(x: Double): Double

  protected def belongsToDomain(x: Double): Boolean = x >= lb && x <= ub
}

sealed case class Spline1() extends Spline(0, 1) {
  override def functionValueAt(x: Double): Double = 0.5 * x * x

  override def firstDerivativeValueAt(x: Double): Double = if(belongsToDomain(x)) x else 0

  override def secondDerivativeValueAt(x: Double): Double = if (belongsToDomain(x)) 1.0 else 0
}

sealed case class Spline2() extends Spline(0, 1) {
  override def functionValueAt(x: Double): Double = (-2 * (x + 1) * (x + 1) + 6 * (x + 1) - 3) * 0.5

  override def firstDerivativeValueAt(x: Double): Double = if (belongsToDomain(x)) 1 - 2 * x else 0

  override def secondDerivativeValueAt(x: Double): Double = if (belongsToDomain(x)) - 2.0 else 0
}

sealed case class Spline3() extends Spline(0, 1) {
  override def functionValueAt(x: Double): Double = 0.5 * (1 - x) * (1 - x)

  override def firstDerivativeValueAt(x: Double): Double = if (belongsToDomain(x)) x - 1 else 0

  override def secondDerivativeValueAt(x: Double): Double = if (belongsToDomain(x)) 1.0 else 0
}

object Spline {

  private val VALUE_OUTSIDE_DOMAIN: Double = 0

  val Spline1T = Spline1()
  val Spline2T = Spline2()
  val Spline3T = Spline3()

}
