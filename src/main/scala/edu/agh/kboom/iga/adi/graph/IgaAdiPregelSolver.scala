package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.monitoring.{StageAccumulator, StageInfoReader}
import edu.agh.kboom.iga.adi.graph.problems.{HeatTransferProblem, ProblemFactory}
import edu.agh.kboom.iga.adi.graph.solver._
import edu.agh.kboom.iga.adi.graph.solver.core._
import org.apache.spark.graphx.GraphXUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

object IgaAdiPregelSolver {

  private val Log = LoggerFactory.getLogger(IgaAdiPregelSolver.getClass)

  def main(args: Array[String]) {
    implicit val cfg: SolverConfig = SolverConfig.LoadedSolverConfig
    val scfg = cfg.spark

    implicit val sc = Some(new SparkConf())
      .map(
        _.setAppName("IGA ADI Pregel Solver")
          .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
          .set("spark.kryo.registrator", "edu.agh.kboom.iga.adi.graph.serialization.IgaAdiKryoRegistrator")
          .set("spark.kryo.registrationRequired", "true")
//          .set("spark.eventLog.dir", "file:///Users/kbhit/Downloads")
//          .set("spark.eventLog.enabled", "true")
      )
      .map(conf => {
        GraphXUtils.registerKryoClasses(conf)
        conf
      })
      .map(conf => scfg.master.map(conf.setMaster).getOrElse(conf))
      .map(conf => scfg.jars.map(conf.set("spark.jars", _)).getOrElse(conf))
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
