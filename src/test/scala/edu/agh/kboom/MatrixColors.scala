package edu.agh.kboom

import edu.agh.kboom.MatrixUtils.{entry, fill}

trait MatrixColors {

  val GreyFabric: (Int, Int) => Double = fill(3)
  val WhiteFabric: (Int, Int) => Double = fill(7)

  val RedFeature: (Int, Int) => Double = entry(0, 0)(-7)
  val BlueFeature: (Int, Int) => Double = entry(6, 6)(-15)
  val GreenFeature: (Int, Int) => Double = entry(3, 3)(-9)

}
