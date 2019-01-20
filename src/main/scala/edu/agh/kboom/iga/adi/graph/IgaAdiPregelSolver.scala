package edu.agh.kboom.iga.adi.graph

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
          .set("spark.kryo.registrator", "edu.agh.kboom.iga.adi.graph.serialization.IgaAdiKryoRegistrator")
          .set("spark.kryo.registrationRequired", "true")
          .set("spark.kryo.unsafe", "true")
          .set("spark.kryoserializer.buffer", "24m")
      )
      .map(conf => {
        GraphXUtils.registerKryoClasses(conf)
        conf
      })
      .map(conf => scfg.master.map(conf.setMaster).getOrElse(conf))
      .map(conf => scfg.jars.map(conf.set("spark.jars", _)).getOrElse(conf))
      .map(new SparkContext(_)).get

    val problemConfig = SolverConfig.LoadedSolverConfig.problem
    val problemSize = problemConfig.size
    val mesh = Mesh(problemSize, problemSize, problemSize, problemSize)

    val iterativeSolver = IterativeSolver(StepSolver(DirectionSolver(mesh)))

    val initialProblem = ProblemFactory.initialProblem(problemConfig)

    val steps = iterativeSolver.solve(initialProblem, (_, step) => step match {
      case StepInformation(step, _) if step < problemConfig.steps => Some(HeatTransferProblem(mesh))
      case _ => None
    })

    val totalInSeconds = (System.currentTimeMillis() - steps.head.timer.start) / 1000

    println(f"Total time (s): ${totalInSeconds}")

    sc.stop()
  }

}
