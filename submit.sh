#!/usr/bin/env bash
bin/spark-submit \
    --master k8s://http://localhost:8001 \
    --deploy-mode cluster \
    --name iga-adi-graph \
    --driver-cores 3 \
    --driver-memory 6G \
    --executor-cores 3 \
    --executor-memory 2G \
    --conf spark.executor.instances=7 \
    --conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
    --conf spark.kubernetes.container.image.pullPolicy=Always \
    --conf spark.kubernetes.container.image=kbhit/spark \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    local:///opt/iga-adi-pregel.jar &