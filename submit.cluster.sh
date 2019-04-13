#!/usr/bin/env bash
spark-submit \
    --master yarn \
    --deploy-mode cluster \
    --name iga-adi-graph \
    --driver-cores 2 \
    --driver-memory 2G \
    --executor-cores 2 \
    --executor-memory 2G \
    --num-executors 1 \
    --conf spark.executor.extraJavaOptions="" \
    --conf spark.driver.extraJavaOptions="-Dproblem.size=768 -Dproblem.steps=1" \
    --conf spark.kryo.unsafe=true \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    --jars iga-adi-graphx-assembly-0.1.0.jar \
    s3n://iga-adi/iga-adi-graphx-assembly-0.1.0.jar