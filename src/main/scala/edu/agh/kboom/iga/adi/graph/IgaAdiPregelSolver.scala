package edu.agh.kboom.iga.adi.graph

import edu.agh.kboom.iga.adi.graph.monitoring.{NetworkSparkListener, StageInfoReader}
import edu.agh.kboom.iga.adi.graph.problems.{HeatTransferProblem, ProblemFactory}
import edu.agh.kboom.iga.adi.graph.solver._
import edu.agh.kboom.iga.adi.graph.solver.core._
import org.apache.spark.graphx.GraphXUtils
import org.apache.spark.{SparkConf, SparkContext}

object IgaAdiPregelSolver {

  def main(args: Array[String]) {
    val cfg = SolverConfig.LoadedSolverConfig
    val scfg = cfg.spark

    implicit val sc = Some(new SparkConf())
      .map(
        _.setAppName("IGA ADI Pregel Solver")
          .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
          //          .set("spark.kryo.classesToRegister", "org.apache.spark.graphx.impl.ShippableVertexPartition,org.apache.spark.util.collection.OpenHashSet.class")
          .set("spark.kryo.registrator", "edu.agh.kboom.iga.adi.graph.serialization.IgaAdiKryoRegistrator")
          //          .set("spark.kryo.registrationRequired", "true")
          .set("spark.kryo.unsafe", "true")
      )
      .map(conf => {
        GraphXUtils.registerKryoClasses(conf)
        conf
      })
      .map(conf => scfg.master.map(conf.setMaster).getOrElse(conf))
      .map(conf => scfg.jars.map(conf.set("spark.jars", _)).getOrElse(conf))
      .map(new SparkContext(_)).get

    val networkListener = new NetworkSparkListener()
    if (cfg.logging.spark) {
      //      sc.addSparkListener(JobSparkListener)
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

    println(StageInfoReader.asString("Most shuffles", networkListener.stagesByShuffles().head))

    println(f"Total time (s): ${System.currentTimeMillis() - sc.startTime}")
    sc.stop()
  }

}
