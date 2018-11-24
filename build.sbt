name          := "iga-adi-graphx"
organization  := "edu.agh.kboom"
description   := "Alternate Directions Implicit Solver For Isogeometric Analysis"
version       := "0.1.0"
scalaVersion  := "2.11.8"
scalacOptions := Seq("-deprecation", "-unchecked", "-encoding", "utf8", "-Xlint")
excludeFilter in unmanagedSources := (HiddenFileFilter || "*-script.scala")
unmanagedResourceDirectories in Compile += baseDirectory.value / "conf"
unmanagedResourceDirectories in Test += baseDirectory.value / "conf"
fork := true
parallelExecution in Test := false

val sparkVersion = "2.3.2"
val scalaTestVersion = "3.0.5"
val scalaCheckVersion = "1.13.4"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,
  "org.apache.spark" %% "spark-repl" % sparkVersion,
  "org.apache.spark" %% "spark-graphx" % sparkVersion,

  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test"
)