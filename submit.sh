#!/usr/bin/env bash
# https://spark.apache.org/docs/latest/tuning.html
# https://spark.apache.org/docs/latest/configuration.html
# https://umbertogriffo.gitbooks.io/apache-spark-best-practices-and-tuning/content/which_storage_level_to_choose.html
# https://jaceklaskowski.gitbooks.io/mastering-apache-spark/spark-rdd-partitions.html
# https://databricks.com/blog/2015/06/22/understanding-your-spark-application-through-visualization.html
bin/spark-submit \
    --master k8s://http://localhost:8001 \
    --deploy-mode cluster \
    --name iga-adi-graph \
    --driver-cores 3 \
    --driver-memory 5G \
    --executor-cores 3 \
    --executor-memory 5G \
    --conf spark.executor.instances=6 \
    --conf spark.default.parallelism=108 \
    --conf spark.kubernetes.container.image.pullPolicy=Always \
    --conf spark.kubernetes.container.image=kbhit/spark \
    --conf spark.scheduler.minRegisteredResourcesRatio=1.0 \
    --conf spark.scheduler.maxRegisteredResourcesWaitingTime=300s \
    --files /opt/metrics.properties \
    --conf spark.metrics.conf=/opt/metrics.properties \
    --jars /opt/metrics-influxdb.jar,/opt/spark-influx-sink.jar \
    --conf spark.driver.extraClassPath=spark-influx-sink.jar:metrics-influxdb.jar  \
    --conf spark.executor.extraClassPath=/opt/spark-influx-sink.jar:/opt/metrics-influxdb.jar  \
    --conf spark.executor.extraJavaOptions="-XX:+UseG1GC -XX:+UseCompressedOops" \
    --conf spark.driver.extraJavaOptions="-XX:+UseG1GC -XX:+UseCompressedOops -Dproblem.size=3072 -Dproblem.steps=1" \
    --conf spark.locality.wait=3600s \
    --conf spark.locality.wait.node=0 \
    --conf spark.locality.wait.process=0 \
    --conf spark.cleaner.periodicGC.interval=10s \
    --conf spark.graphx.pregel.checkpointInterval=10 \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    local:///opt/iga-adi-pregel.jar &

    -
    --conf spark.cleaner.referenceTracking.blocking=false \
    --conf spark.cleaner.referenceTracking.blocking.shuffle=false \
    --conf spark.default.parallelism=54 \

     --conf spark.executor.extraJavaOptions="-XX:+UseG1GC -XX:+UseCompressedOops -Dlogging.operations=true -XX:+PrintGCTimeStamps -XX:+PrintGCDetails" \
    --conf spark.driver.extraJavaOptions="-XX:+UseG1GC -XX:+UseCompressedOops -Dproblem.size=1536 -Dproblem.steps=1 -Dlogging.operations=true -XX:+PrintGCTimeStamps -XX:+PrintGCDetails " \

    --conf spark.executor.heartbeatInterval=7200000 \
    --conf spark.network.timeout=7200000 \