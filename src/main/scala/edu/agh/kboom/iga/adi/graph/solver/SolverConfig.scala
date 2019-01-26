package edu.agh.kboom.iga.adi.graph.solver

import java.text.SimpleDateFormat
import java.util.Calendar

sealed case class OutputConfig(store: Boolean, dir: String) {

  private val dateFormat = new SimpleDateFormat("yyyyMMddHHmmss")
  private val timeStamp = dateFormat.format(Calendar.getInstance().getTime())

  def filenameFor(stepInformation: StepInformation): String = f"$dir/$timeStamp/${stepInformation.step}"

}

sealed trait ProblemConfig {
  def size: Int

  def steps: Int
}

final case class HeatTransferProblemConfig(size: Int, steps: Int) extends ProblemConfig
final case class ProjectionProblemConfig(size: Int, steps: Int) extends ProblemConfig
final case class LoggingConfig(operations: Boolean, elements: Boolean, surfaces: Boolean, spark: Boolean)
final case class SparkConfig(master: Option[String], jars: Option[String])
final case class SolverConfig(problem: ProblemConfig, output: OutputConfig, logging: LoggingConfig, spark: SparkConfig)

object SolverConfig {

  import pureconfig.generic.auto._

  val LoadedSolverConfig: SolverConfig = load()

  private def load(): SolverConfig = pureconfig.loadConfig[SolverConfig] match {
    case Right(config) => config
    case Left(failures) => throw new IllegalStateException(f"Could not load config $failures")
  }

}

