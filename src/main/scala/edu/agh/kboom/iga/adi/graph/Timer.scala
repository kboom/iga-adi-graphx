package edu.agh.kboom.iga.adi.graph

import java.text.SimpleDateFormat

import edu.agh.kboom.iga.adi.graph.Timer.dateFormat

case class Timer(start: Long) {

  def startTimestamp(): String = dateFormat.format(start)

}

object Timer {

  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")

  def start(): Timer = Timer(System.currentTimeMillis())

}
