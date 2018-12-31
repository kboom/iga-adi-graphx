addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)).evaluated
