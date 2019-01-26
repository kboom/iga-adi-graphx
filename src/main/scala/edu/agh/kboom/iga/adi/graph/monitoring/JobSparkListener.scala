package edu.agh.kboom.iga.adi.graph.monitoring

import org.apache.spark.scheduler.{SparkListener, SparkListenerJobStart}
import org.slf4j.LoggerFactory

object JobSparkListener extends SparkListener {
  private val Log = LoggerFactory.getLogger(getClass)

  override def onJobStart(jobStart: SparkListenerJobStart): Unit = {
    Log.debug(s"${jobStart.jobId} (${jobStart.time/1000}s) => stages: ${jobStart.stageIds.count(_ => true)}")
  }

  private def totalReads(jobStart: SparkListenerJobStart) =
    jobStart.stageInfos.map(_.taskMetrics).map(_.shuffleReadMetrics).map(_.remoteBytesRead).sum

}
