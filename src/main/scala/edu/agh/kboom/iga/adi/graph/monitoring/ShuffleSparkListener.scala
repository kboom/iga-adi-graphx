package edu.agh.kboom.iga.adi.graph.monitoring

import org.apache.spark.scheduler.{SparkListener, SparkListenerStageCompleted}
import org.slf4j.LoggerFactory

object ShuffleSparkListener extends SparkListener  {
  private val Log = LoggerFactory.getLogger(getClass)

  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
    val metrics = stageCompleted.stageInfo.taskMetrics.shuffleReadMetrics
    if(metrics.totalBlocksFetched > 0) {
      Log.debug(s"Fetch: t: ${metrics.totalBlocksFetched} r: ${metrics.remoteBlocksFetched} l: ${metrics.localBlocksFetched}")
    }
  }
}
