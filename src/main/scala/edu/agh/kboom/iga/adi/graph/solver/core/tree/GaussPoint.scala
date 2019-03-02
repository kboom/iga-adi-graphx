package edu.agh.kboom.iga.adi.graph.solver.core.tree

import breeze.linalg.DenseMatrix
import edu.agh.kboom.iga.adi.graph.solver.core.Spline.{ Spline1T, Spline2T, Spline3T }

case class GaussPoint(v: Double, w: Double)

object GaussPoint {

  val gaussPoints = Array(
    GaussPoint((1.0 - 0.973906528517171720077964) * 0.5, 0.0666713443086881375935688 * 0.5),
    GaussPoint((1.0 - 0.8650633666889845107320967) * 0.5, 0.1494513491505805931457763 * 0.5),
    GaussPoint((1.0 - 0.6794095682990244062343274) * 0.5, 0.2190863625159820439955349 * 0.5),
    GaussPoint((1.0 - 0.4333953941292471907992659) * 0.5, 0.2692667193099963550912269 * 0.5),
    GaussPoint((1.0 - 0.1488743389816312108848260) * 0.5, 0.2955242247147528701738930 * 0.5),
    GaussPoint((1.0 + 0.1488743389816312108848260) * 0.5, 0.2955242247147528701738930 * 0.5),
    GaussPoint((1.0 + 0.4333953941292471907992659) * 0.5, 0.2692667193099963550912269 * 0.5),
    GaussPoint((1.0 + 0.6794095682990244062343274) * 0.5, 0.2190863625159820439955349 * 0.5),
    GaussPoint((1.0 + 0.8650633666889845107320967) * 0.5, 0.1494513491505805931457763 * 0.5),
    GaussPoint((1.0 + 0.9739065285171717200779640) * 0.5, 0.0666713443086881375935688 * 0.5)
  )

  val gaussPointCount: Int = gaussPoints.length


  val S11 = DenseMatrix.tabulate(
    GaussPoint.gaussPointCount,
    GaussPoint.gaussPointCount
  ){ case (k, l) => {
    val gpk = GaussPoint.gaussPoints(k)
    val gpl = GaussPoint.gaussPoints(l)
    gpk.w * Spline1T.getValue(gpk.v) * gpl.w * Spline1T.getValue(gpl.v)
  } }

  val S12 = DenseMatrix.tabulate(
    GaussPoint.gaussPointCount,
    GaussPoint.gaussPointCount
  ){ case (k, l) => {
    val gpk = GaussPoint.gaussPoints(k)
    val gpl = GaussPoint.gaussPoints(l)
    gpk.w * Spline1T.getValue(gpk.v) * gpl.w * Spline2T.getValue(gpl.v)
  } }

  val S13 = DenseMatrix.tabulate(
    GaussPoint.gaussPointCount,
    GaussPoint.gaussPointCount
  ){ case (k, l) => {
    val gpk = GaussPoint.gaussPoints(k)
    val gpl = GaussPoint.gaussPoints(l)
    gpk.w * Spline1T.getValue(gpk.v) * gpl.w * Spline3T.getValue(gpl.v)
  } }

  val S21 = DenseMatrix.tabulate(
    GaussPoint.gaussPointCount,
    GaussPoint.gaussPointCount
  ){ case (k, l) => {
    val gpk = GaussPoint.gaussPoints(k)
    val gpl = GaussPoint.gaussPoints(l)
    gpk.w * Spline2T.getValue(gpk.v) * gpl.w * Spline1T.getValue(gpl.v)
  } }

  val S22 = DenseMatrix.tabulate(
    GaussPoint.gaussPointCount,
    GaussPoint.gaussPointCount
  ){ case (k, l) => {
    val gpk = GaussPoint.gaussPoints(k)
    val gpl = GaussPoint.gaussPoints(l)
    gpk.w * Spline2T.getValue(gpk.v) * gpl.w * Spline2T.getValue(gpl.v)
  } }

  val S23 = DenseMatrix.tabulate(
    GaussPoint.gaussPointCount,
    GaussPoint.gaussPointCount
  ){ case (k, l) => {
    val gpk = GaussPoint.gaussPoints(k)
    val gpl = GaussPoint.gaussPoints(l)
    gpk.w * Spline2T.getValue(gpk.v) * gpl.w * Spline3T.getValue(gpl.v)
  } }

  val S31 = DenseMatrix.tabulate(
    GaussPoint.gaussPointCount,
    GaussPoint.gaussPointCount
  ){ case (k, l) => {
    val gpk = GaussPoint.gaussPoints(k)
    val gpl = GaussPoint.gaussPoints(l)
    gpk.w * Spline3T.getValue(gpk.v) * gpl.w * Spline1T.getValue(gpl.v)
  } }

  val S32 = DenseMatrix.tabulate(
    GaussPoint.gaussPointCount,
    GaussPoint.gaussPointCount
  ){ case (k, l) => {
    val gpk = GaussPoint.gaussPoints(k)
    val gpl = GaussPoint.gaussPoints(l)
    gpk.w * Spline3T.getValue(gpk.v) * gpl.w * Spline2T.getValue(gpl.v)
  } }

  val S33 = DenseMatrix.tabulate(
    GaussPoint.gaussPointCount,
    GaussPoint.gaussPointCount
  ){ case (k, l) => {
    val gpk = GaussPoint.gaussPoints(k)
    val gpl = GaussPoint.gaussPoints(l)
    gpk.w * Spline3T.getValue(gpk.v) * gpl.w * Spline3T.getValue(gpl.v)
  } }

}