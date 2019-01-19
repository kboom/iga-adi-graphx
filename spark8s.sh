#!/usr/bin/env bash
bin/spark-submit \
    --master k8s://http://localhost:8001 \
    --deploy-mode cluster \
    --name iga-adi-graph \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    --conf spark.executor.instances=6 \
    --conf spark.kubernetes.container.image=kbhit/iga-adi-graphx \
    local:///usr/src/app/iga-adi-pregel.jar