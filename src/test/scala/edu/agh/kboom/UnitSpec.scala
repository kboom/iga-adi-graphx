package edu.agh.kboom

import org.scalatest._

abstract class UnitSpec extends WordSpec with Matchers with
  OptionValues with Inside with Inspectors
