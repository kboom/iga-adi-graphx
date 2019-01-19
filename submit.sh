#!/usr/bin/env bash
bin/spark-submit \
    --master k8s://http://localhost:8001 \
    --deploy-mode cluster \
    --name iga-adi-graph \
    --executor-memory 20G \
    --total-executor-cores 10 \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    --conf spark.kubernetes.container.image=kbhit/spark \
    file:///Users/kbhit/Sources/phd/iga-adi-graphx/target/scala-2.11/iga-adi-graphx_2.11-0.1.0.jar