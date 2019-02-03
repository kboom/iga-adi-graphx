package edu.agh.kboom.iga.adi.graph

object TimeEventType extends Enumeration {
  type TimeEventType = Value
  val STEP_STARTED, HORIZONTAL_STARTED, TRANSPOSITION_STARTED, VERTICAL_STARTED,
  STEP_FINISHED, INITIALIZED_HORIZONTAL, INITIALIZED_VERTICAL = Value
}
