#!/usr/bin/env bash
GC_OPTS="-XX:+UseG1GC -XX:+UseCompressedOops -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication -XX:MaxGCPauseMillis=500 -XX:ParallelGCThreads=8 -XX:ConcGCThreads=8 -XX:G1HeapRegionSize=4m -XX:+ParallelRefProcEnabled -XX:-ResizePLAB -XX:+ExplicitGCInvokesConcurrent"
SPARK_CHECKPOINT_DIR="/mnt/sparkl"
SPARK_OPTS="-Djava.io.tmpdir=/mnt/sparkl -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"
LOGS=""
SIZE=12288

for EXEC in 10
do
for RUN in {1..1}
do
spark-submit \
    --master yarn \
    --deploy-mode client \
    --driver-cores 4 \
    --driver-memory 10G \
    --num-executors $EXEC \
    --executor-cores 4 \
    --executor-memory 18G \
    --conf spark.executor.extraJavaOptions="${SPARK_OPTS} ${GC_OPTS} ${LOGS}" \
    --conf spark.driver.extraJavaOptions="-Dproblem.size=12288 -Dproblem.steps=1 ${SPARK_OPTS} ${GC_OPTS} ${LOGS}" \
    --conf spark.kryo.unsafe=true \
    --conf spark.local.dir=/mnt/sparkl \
    --conf spark.cleaner.referenceTracking.cleanCheckpoints=true \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    --jars iga-adi-graphx-assembly-0.1.0.jar \
    --conf spark.scheduler.minRegisteredResourcesRatio=1.0 \
    --conf spark.scheduler.maxRegisteredResourcesWaitingTime=300s \
    --conf spark.default.parallelism=$(echo "$((8 * ${EXEC}))") \
    --conf spark.cleaner.referenceTracking.blocking=false \
    --conf spark.memory.storageFraction=0.4 \
    --conf spark.executor.heartbeatInterval=36000s \
    --conf spark.network.timeout=360000s \
    --conf spark.memory.offHeap.size=4G \

    iga-adi-graphx-assembly-0.1.0.jar &> "lessjobAndTuning-${SIZE}-${EXEC}-${RUN}.txt"
done
done