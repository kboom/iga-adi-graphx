#!/usr/bin/env bash
spark-submit \
    --master yarn \
    --deploy-mode cluster \
    --name iga-adi-graph \
    --driver-cores 1 \
    --driver-memory 1G \
    --executor-cores 1 \
    --executor-memory 1G \
    --num-executors 1 \
    --conf spark.executor.extraJavaOptions="" \
    --conf spark.driver.extraJavaOptions="-Dproblem.size=12 -Dproblem.steps=1" \
    --conf spark.kryo.unsafe=true \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    --jars iga-adi-graphx-assembly-0.1.0.jar \
    local:///home/ec2-user/iga-adi-graphx-assembly-0.1.0.jar