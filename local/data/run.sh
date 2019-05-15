#!/bin/bash
RUN="${RUN:-1}"
EXEC="${EXEC:-2}"
EXEC_CORES="${EXEC_CORES:-2}"
EXEC_MEMORY="${EXEC_MEMORY:-1G}"
DRIVER_MEMORY="${DRIVER_MEMORY:-1G}"

spark-submit \
    --master spark://172.19.0.2:7077 \
    --deploy-mode client \
    --driver-cores 1 \
    --driver-memory $DRIVER_MEMORY \
    --num-executors $EXEC \
    --executor-cores $EXEC_CORES \
    --executor-memory $EXEC_MEMORY \
    --conf spark.executor.extraJavaOptions="-XX:+UseG1GC -XX:+UseCompressedOops -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication -XX:InitiatingHeapOccupancyPercent=0 -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=8 -XX:ConcGCThreads=8 -XX:G1ReservePercent=10 -XX:G1HeapRegionSize=16m" \
    --conf spark.driver.extraJavaOptions="-Dproblem.size=48 -XX:+UseCompressedOops -Dproblem.steps=1 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication -XX:InitiatingHeapOccupancyPercent=0 -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=8 -XX:ConcGCThreads=8 -XX:G1ReservePercent=10 -XX:G1HeapRegionSize=8m" \
    --conf spark.kryo.unsafe=true \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    --conf spark.scheduler.minRegisteredResourcesRatio=1.0 \
    --conf spark.scheduler.maxRegisteredResourcesWaitingTime=300s \
    --conf spark.default.parallelism=$(echo "$((${EXEC_CORES} * ${EXEC}))") \
    --conf spark.cleaner.referenceTracking.blocking=false \
    --conf spark.memory.fraction=0.7 \
    --conf spark.locality.wait=999999999s \
    --conf spark.eventLog.enabled=true \
    --conf spark.eventLog.dir=/tmp/data/logs \
    ${@} \
    iga-adi-graphx-assembly-0.1.0.jar #&> "${EXEC}-nodes-48-${EXEC_CORES}-${RUN}.txt"