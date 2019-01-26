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
       | executorCPU: ${s.taskMetrics.executorCpuTime / 1000000}
       | peakMemory: ${s.taskMetrics.peakExecutionMemory}
       | gctime: ${s.taskMetrics.jvmGCTime}
       | rdd: ${s.rddInfos.map(_.name).mkString(",")}
       | shuffle:
       | - records read: ${s.taskMetrics.shuffleReadMetrics.recordsRead}
       | - total blocks fetched: ${s.taskMetrics.shuffleReadMetrics.totalBlocksFetched}
       | ${s.details}
       |""".stripMargin

}
