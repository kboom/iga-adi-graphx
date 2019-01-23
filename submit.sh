#!/usr/bin/env bash
# https://spark.apache.org/docs/latest/configuration.html
# https://umbertogriffo.gitbooks.io/apache-spark-best-practices-and-tuning/content/which_storage_level_to_choose.html
# https://jaceklaskowski.gitbooks.io/mastering-apache-spark/spark-rdd-partitions.html
# https://databricks.com/blog/2015/06/22/understanding-your-spark-application-through-visualization.html
bin/spark-submit \
    --master k8s://http://localhost:8001 \
    --deploy-mode cluster \
    --name iga-adi-graph \
    --driver-cores 3 \
    --driver-memory 6G \
    --executor-cores 3 \
    --executor-memory 6G \
    --conf spark.executor.instances=6 \
    --conf spark.kubernetes.container.image.pullPolicy=Always \
    --conf spark.kubernetes.container.image=kbhit/spark \
    --conf spark.kryoserializer.buffer=24m \
    --conf spark.kryo.referenceTracking=false \
    --conf spark.cleaner.periodicGC.interval=30s \
    --conf spark.scheduler.maxRegisteredResourcesWaitingTime=120s \
    --conf spark.scheduler.minRegisteredResourcesRatio=1.0 \
    --conf spark.rpc.message.maxSize=1000 \
    --conf spark.network.timeout=10000s \
    --conf spark.default.parallelism=30 \
    --conf spark.executor.extraJavaOptions="-XX:+UseCompressedOops" \
    --conf spark.driver.extraJavaOptions="-XX:+UseCompressedOops -Dproblem.size=192 -Dproblem.steps=100" \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    local:///opt/iga-adi-pregel.jar &