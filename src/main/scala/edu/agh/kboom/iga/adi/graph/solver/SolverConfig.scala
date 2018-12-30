package edu.agh.kboom.iga.adi.graph.solver

import java.text.SimpleDateFormat
import java.util.Calendar

sealed case class OutputConfig(dir: String) {

  private val dateFormat = new SimpleDateFormat("yyyymmddHHmmss")

  def filenameFor(stepInformation: StepInformation): String = f"$dir/${dateFormat.format(Calendar.getInstance().getTime())}/${stepInformation.step}.data"

}

sealed trait ProblemConfig {
  def size: Int

  def steps: Int
}

final case class HeatTransferProblemConfig(size: Int, steps: Int) extends ProblemConfig

final case class ProjectionProblemConfig(size: Int, steps: Int) extends ProblemConfig

sealed case class SolverConfig(problem: ProblemConfig, output: OutputConfig)

object SolverConfig {

  import pureconfig.generic.auto._

  val LoadedSolverConfig = load()

  private def load(): SolverConfig = pureconfig.loadConfig[SolverConfig] match {
    case Right(config) => config
    case Left(failures) => throw new IllegalStateException(f"Could not load config $failures")
  }

}