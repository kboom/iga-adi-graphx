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
    --executor-memory 6G \
    --conf spark.executor.instances=10 \
    --conf spark.default.parallelism=30 \
    --conf spark.kubernetes.executor.request.cores=3000m \
    --conf spark.kubernetes.executor.limit.cores=3000m \
    --conf spark.kubernetes.memoryOverheadFactor=0.2 \
    --conf spark.kubernetes.container.image.pullPolicy=Always \
    --conf spark.kubernetes.container.image=kbhit/iga-adi-pregel \
    --conf spark.scheduler.minRegisteredResourcesRatio=1.0 \
    --conf spark.scheduler.maxRegisteredResourcesWaitingTime=300s \
    --files /opt/metrics.properties \
    --conf spark.metrics.conf=/opt/metrics.properties \
    --jars /opt/metrics-influxdb.jar,/opt/spark-influx-sink.jar \
    --conf spark.driver.extraClassPath=spark-influx-sink.jar:metrics-influxdb.jar  \
    --conf spark.executor.extraClassPath=/opt/spark-influx-sink.jar:/opt/metrics-influxdb.jar  \
    --conf spark.executor.extraJavaOptions="" \
    --conf spark.driver.extraJavaOptions="-Dproblem.size=3072 -Dproblem.steps=1" \
    --conf spark.kryo.unsafe=true \
    --conf spark.kryoserializer.buffer=32m \
    --conf spark.network.timeout=360s \
    --conf spark.memory.fraction=0.5 \
    --conf spark.locality.wait.node=0 \
    --conf spark.locality.wait=9999999 \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    local:///opt/iga-adi-pregel.jar &


    --conf spark.locality.wait=9999999 \
    --conf spark.memory.fraction=0.6 \

        --conf spark.locality.wait.process=0 \
    --conf spark.locality.wait=999999 \

# Observations
- Increasing partitions breaks tree partitioning as vertices are scattered accross multiple nodes (over long distance)
- Locality wait should be inifiinte as it is a well-posed algorithm with predefined partitioning scheme in a homogenous cluster


# Tested
--spark.kryo.unsafe=true <- makes kryo equally fast as Java
--spark.locality.wait=999999 <- solver won't break but computations will be really slow with unevenly distributed tasks
--conf spark.locality.wait.process=0 <- this has no practical effect unless we schedule multiple workers per node which does not make sense anyways


# To try
Use IN_MEMORY_SER to really levarage kryo

--conf spark.locality.wait=0 \
--conf spark.kryoserializer.buffer=64m \

# Tried
--conf spark.shuffle.spill.compress=false \ (increases by 30%)



    --conf spark.locality.wait=3600s \
    --conf spark.locality.wait.node=0 \
    --conf spark.locality.wait.process=0 \
    --conf spark.cleaner.periodicGC.interval=10s \
    --conf spark.graphx.pregel.checkpointInterval=10 \

    -
    --conf spark.cleaner.referenceTracking.blocking=false \
    --conf spark.cleaner.referenceTracking.blocking.shuffle=false \
    --conf spark.default.parallelism=54 \

     --conf spark.executor.extraJavaOptions="-XX:+UseG1GC -XX:+UseCompressedOops -Dlogging.operations=true -XX:+PrintGCTimeStamps -XX:+PrintGCDetails" \
    --conf spark.driver.extraJavaOptions="-XX:+UseG1GC -XX:+UseCompressedOops -Dproblem.size=1536 -Dproblem.steps=1 -Dlogging.operations=true -XX:+PrintGCTimeStamps -XX:+PrintGCDetails " \

    --conf spark.executor.heartbeatInterval=7200000 \
    --conf spark.network.timeout=7200000 \