package edu.agh.kboom

import org.scalatest._

abstract class SubjectSpec extends WordSpec with Matchers with
  OptionValues with Inside with Inspectors

abstract class MethodSpec extends FunSpec with Matchers