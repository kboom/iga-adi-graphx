name          := "iga-adi-graphx"
organization  := "edu.agh.kboom"
description   := "Alternate Directions Implicit Solver For Isogeometric Analysis"
version       := "0.1.0"
scalaVersion  := "2.11.8"
scalacOptions := Seq("-deprecation", "-unchecked", "-encoding", "utf8", "-Xlint")
mainClass := Some("edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver")
excludeFilter in unmanagedSources := (HiddenFileFilter || "*-script.scala")
unmanagedResourceDirectories in Compile += baseDirectory.value / "conf"
unmanagedResourceDirectories in Test += baseDirectory.value / "conf"
fork := true
parallelExecution in Test := false

val sparkVersion = "2.4.3"
val scalaTestVersion = "3.0.5"
val scalaCheckVersion = "1.13.4"
val pureConfigVersion = "0.10.1"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-hive" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-repl" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-graphx" % sparkVersion % "provided",
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,

  "org.scalanlp" %% "breeze" % "0.13.2",
  "org.scalanlp" %% "breeze-natives" % "0.13.2",

  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test"
)
