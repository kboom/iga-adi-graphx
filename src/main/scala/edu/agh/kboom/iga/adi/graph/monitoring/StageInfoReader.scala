package edu.agh.kboom.iga.adi.graph.monitoring

import org.apache.spark.scheduler.StageInfo

object StageInfoReader {

  def gctimeRatio(s: StageInfo): Double = if( s.taskMetrics.executorRunTime != 0) (s.taskMetrics.jvmGCTime + 0.0) / s.taskMetrics.executorRunTime else 0
  def fetchRuntimeRatio(s: StageInfo): Double = if(s.taskMetrics.executorRunTime != 0) (s.taskMetrics.shuffleReadMetrics.fetchWaitTime + 0.0) / s.taskMetrics.executorRunTime else 0
  def localFetchRatio(s: StageInfo): Double = if(s.taskMetrics.shuffleReadMetrics.totalBlocksFetched != 0) (s.taskMetrics.shuffleReadMetrics.remoteBlocksFetched + 0.0) / s.taskMetrics.shuffleReadMetrics.totalBlocksFetched else 0
  def peakMemory(s: StageInfo): Long = s.taskMetrics.peakExecutionMemory / 1000000

  def asString(t: String, s: StageInfo): String =
    s"""
       |--------------------------------------------
       | $t
       |  Stage ${s.name}
       | -------------------------------------------
       | tasks: ${s.numTasks}
       | run time: ${s.taskMetrics.executorRunTime}ms
       | gctime ratio: ${gctimeRatio(s)*100}%
       | fetch / runtime: ${fetchRuntimeRatio(s)*100}%
       | peakMemory: ${peakMemory(s)}MB
       | rdd: ${s.rddInfos.map(_.name).mkString(",")}
       | shuffle:
       | - total time: ${s.taskMetrics.shuffleWriteMetrics.writeTime / Math.pow(10,6) + s.taskMetrics.shuffleReadMetrics.fetchWaitTime /Math.pow(10,3)}ms
       | - local blocks fetched ratio: ${localFetchRatio(s) * 100}%
       | - records read: ${s.taskMetrics.shuffleReadMetrics.recordsRead}
       | - records written: ${s.taskMetrics.shuffleWriteMetrics.recordsWritten}
       | ${s.details}
       |""".stripMargin

}
