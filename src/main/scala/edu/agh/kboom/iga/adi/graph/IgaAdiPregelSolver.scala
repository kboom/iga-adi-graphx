package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.monitoring.{StageAccumulator, StageInfoReader}
import edu.agh.kboom.iga.adi.graph.problems.{HeatTransferProblem, ProblemFactory}
import edu.agh.kboom.iga.adi.graph.solver._
import edu.agh.kboom.iga.adi.graph.solver.core._
import org.apache.spark.graphx.GraphXUtils
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

object IgaAdiPregelSolver {

  private val Log = LoggerFactory.getLogger(IgaAdiPregelSolver.getClass)

  private val InfiniteWait = "9999999999s"

  def main(args: Array[String]) {
    implicit val cfg: SolverConfig = SolverConfig.LoadedSolverConfig
    val scfg = cfg.spark
    implicit val sc = Some(new SparkConf())
      .map(
        _.setAppName("IGA ADI Pregel Solver")
          .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
          .set("spark.kryo.registrator", "edu.agh.kboom.iga.adi.graph.serialization.IgaAdiKryoRegistrator")
          .set("spark.kryo.registrationRequired", "true")
          .set("spark.kryo.unsafe", "true")
          .set("spark.cleaner.referenceTracking.blocking", "false")
          .set("spark.scheduler.minRegisteredResourcesRatio", "1.0")
          .set("spark.locality.wait", InfiniteWait)
          .setIfMissing("spark.scheduler.maxRegisteredResourcesWaitingTime", InfiniteWait)
          .setIfMissing("spark.worker.cleanup.enabled", "true")
          .setIfMissing("spark.deploy.spreadOut", "false") // align partitions next to each other, worker by worker rather than doing round robin
          .setIfMissing("spark.eventLog.enabled", "true")
          .setIfMissing("spark.eventLog.dir", s"file:///${System.getProperty("sparklogs", System.getProperty("java.io.tmpdir"))}")
      )
      .map(conf => {
        GraphXUtils.registerKryoClasses(conf)
        conf
      })
      .map(conf => scfg.master.map(conf.setMaster).getOrElse(conf))
      .map(conf => scfg.jars.map(conf.setIfMissing("spark.jars", _)).getOrElse(conf))
      .map(conf => conf.setJars(JavaStreamingContext.jarOfClass(getClass)))
      .map(new SparkContext(_)).get

//    val checkpointPath = Paths.get(System.getenv("SPARK_YARN_STAGING_DIR"), "checkpoints").toString

//    sc.setCheckpointDir(System.getenv("SPARK_YARN_STAGING_DIR"))

    // "hdfs:///checkpoints"
    // "wasb:///checkpoints"

//    sc.setCheckpointDir(System.getenv("SPARK_CHECKPOINT_DIR"))

    val networkListener = new StageAccumulator()
    if (cfg.logging.spark) {
      sc.addSparkListener(networkListener)
      //      sc.addSparkListener(ShuffleSparkListener)
    }

    val problemConfig = SolverConfig.LoadedSolverConfig.problem
    val problemSize = problemConfig.size
    val mesh = Mesh(problemSize, problemSize, problemSize, problemSize)

    val iterativeSolver = IterativeSolver(StepSolver(DirectionSolver(mesh)))

    val initialProblem = ProblemFactory.initialProblem(problemConfig)

    val steps = iterativeSolver.solve(initialProblem, (_, step) => step match {
      case StepInformation(step, _) if step < problemConfig.steps => Some(HeatTransferProblem(mesh))
      case _ => None
    })

    Log.info(SolverConfig.describe)
    Log.info(StageInfoReader.asString("Most fetch wait ratio", networkListener.stagesByFetchVsRuntimeRatio().head))
    Log.info(StageInfoReader.asString("Most shuffles", networkListener.stagesByShuffles().head))
    Log.info(StageInfoReader.asString("Top execution time", networkListener.stagesByExecutionTime().head))
    Log.info(StageInfoReader.asString("Peak memory", networkListener.stagesByMemory().head))

    Log.info(f"Total time (ms): ${System.currentTimeMillis() - sc.startTime}")

    steps.foreach(step => Log.info(s"\nStep ${step.step} times:\n${step.timeRecorder.summary().mkString(System.lineSeparator())}\n"))

    sc.cancelAllJobs()
    sc.stop()
  }

}
