#!/usr/bin/env bash
bin/spark-submit \
    --master k8s://http://localhost:8001 \
    --deploy-mode cluster \
    --name iga-adi-graph \
    --driver-cores 3 \
    --driver-memory 6G \
    --executor-cores 3 \
    --executor-memory 3G \
    --conf spark.executor.instances=6 \
    --conf spark.kubernetes.container.image.pullPolicy=Always \
    --conf spark.kubernetes.container.image=kbhit/spark \
    --conf spark.executor.extraJavaOptions="-XX:+UseCompressedOops" \
    --conf spark.driver.extraJavaOptions="-XX:+UseCompressedOops -Dproblem.size=3072 -Dproblem.steps=1" \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    local:///opt/iga-adi-pregel.jar &