package edu.agh.kboom.iga.adi.graph.monitoring

import org.apache.spark.scheduler.StageInfo

object StageInfoReader {

  def asString(t: String, s: StageInfo): String =
    s"""
       |--------------------------------------------
       | $t
       |  Stage ${s.name}
       | -------------------------------------------
       | tasks: ${s.numTasks}
       | run time: ${s.taskMetrics.executorRunTime / 1000000}ms
       | cpu utilisation: ${(s.taskMetrics.executorCpuTime + 0.0) / Math.max(s.taskMetrics.executorCpuTime, s.taskMetrics.executorRunTime * 1000000)}%
       | peakMemory: ${s.taskMetrics.peakExecutionMemory / 1000000}MB
       | gctime: ${s.taskMetrics.jvmGCTime}ms
       | rdd: ${s.rddInfos.map(_.name).mkString(",")}
       | shuffle:
       | - records read: ${s.taskMetrics.shuffleReadMetrics.recordsRead}
       | - total blocks fetched: ${s.taskMetrics.shuffleReadMetrics.totalBlocksFetched}
       | - records written: ${s.taskMetrics.shuffleWriteMetrics.recordsWritten}
       | ${s.details}
       |""".stripMargin

}
