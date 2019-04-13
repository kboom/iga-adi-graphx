#!/usr/bin/env bash
sbt assembly
scp target/scala-2.11/iga-adi-graphx-assembly-0.1.0.jar kbhit@iga-adi-ssh.azurehdinsight.net:/home/kbhit/