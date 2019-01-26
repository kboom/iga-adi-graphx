package edu.agh.kboom.iga.adi.graph.monitoring

import org.apache.spark.scheduler.{SparkListener, SparkListenerStageCompleted, StageInfo}

import scala.collection.mutable.ListBuffer

class StageAccumulator extends SparkListener {
  private val sb = new ListBuffer[StageInfo]()

  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
    sb += stageCompleted.stageInfo
  }

  def stagesByShuffles(): Seq[StageInfo] = sb.sortBy(
    si => - si.taskMetrics.shuffleReadMetrics.totalBlocksFetched
  )

  def stagesByExecutionTime(): Seq[StageInfo] = sb.sortBy(
    si => - si.taskMetrics.executorRunTime
  )

}
