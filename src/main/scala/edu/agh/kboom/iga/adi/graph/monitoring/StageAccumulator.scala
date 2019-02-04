package edu.agh.kboom.iga.adi.graph.monitoring

import edu.agh.kboom.iga.adi.graph.monitoring.StageInfoReader.{fetchRuntimeRatio, peakMemory, localFetchRatio}
import org.apache.spark.scheduler.{SparkListener, SparkListenerStageCompleted, StageInfo}

import scala.collection.mutable.ListBuffer

class StageAccumulator extends SparkListener {
  private val sb = new ListBuffer[StageInfo]()

  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
    sb += stageCompleted.stageInfo
  }

  def stagesByFetchVsRuntimeRatio(): Seq[StageInfo] = sb.sortBy(-fetchRuntimeRatio(_))

  def stagesByShuffles(): Seq[StageInfo] = sb.sortBy(-localFetchRatio(_))

  def stagesByExecutionTime(): Seq[StageInfo] = sb.sortBy(si => - si.taskMetrics.executorRunTime)

  def stagesByMemory(): Seq[StageInfo] = sb.sortBy(-peakMemory(_))

}
