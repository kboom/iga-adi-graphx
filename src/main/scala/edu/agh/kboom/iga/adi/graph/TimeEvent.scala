package edu.agh.kboom.iga.adi.graph

import java.text.SimpleDateFormat

import edu.agh.kboom.iga.adi.graph.TimeEvent.dateFormat
import edu.agh.kboom.iga.adi.graph.TimeEventType._
import edu.agh.kboom.iga.adi.graph.solver.{HORIZONTAL, SolverDirection, VERTICAL}

import scala.collection.mutable.ListBuffer

case class TimeEvent(name: TimeEventType, time: Long) {

  def startTimestamp(): String = dateFormat.format(time)

}


object TimeEvent {

  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")

  def initialized(direction: SolverDirection): TimeEventType = direction match {
    case HORIZONTAL => INITIALIZED_HORIZONTAL
    case VERTICAL => INITIALIZED_VERTICAL
  }

}

class TimeRecorder() {
  val times: ListBuffer[TimeEvent] = ListBuffer.empty[TimeEvent]

  def record(name: TimeEventType): Unit = {
    times += TimeEvent(name, System.currentTimeMillis())
  }

  def summary(): List[(String, Long)] = {
    val eventsByName = times.groupBy(_.name).mapValues(_.head)

    def timeBetween(a: TimeEventType, b: TimeEventType)
      = Seq(b, a).map(eventsByName(_).time).reduce(_ - _)

    List(
      ("Total", timeBetween(STEP_STARTED, STEP_FINISHED)),
      ("Horizontal total", timeBetween(HORIZONTAL_STARTED, VERTICAL_STARTED)),
      ("Vertical total", timeBetween(VERTICAL_STARTED, STEP_FINISHED)),
      ("Transposition", timeBetween(TRANSPOSITION_STARTED, VERTICAL_STARTED)),
      ("Horizontal Initialisation", timeBetween(HORIZONTAL_STARTED, INITIALIZED_HORIZONTAL)),
      ("Vertical Initialisation", timeBetween(VERTICAL_STARTED, INITIALIZED_VERTICAL))
    )
  }
}

object TimeRecorder {
  def empty(): TimeRecorder = new TimeRecorder()
}
