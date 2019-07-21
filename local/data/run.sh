#!/bin/bash
PROBLEM_SIZE="${PROBLEM_SIZE:-1536}"
PROBLEM_STEPS="${PROBLEM_STEPS:-1}"
RUN="${RUN:-1}"
EXEC="${EXEC:-4}"
EXEC_CORES="${EXEC_CORES:-2}"
EXEC_MEMORY="${EXEC_MEMORY:-1G}"
DRIVER_MEMORY="${DRIVER_MEMORY:-1G}"
JVM_OPTS="${JVM_OPTS:-}"
DEBUG="${DEBUG:-false}"

GC_OPTS="-XX:+UseG1GC -XX:+UseCompressedOops -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication -XX:InitiatingHeapOccupancyPercent=0 -XX:MaxGCPauseMillis=500 -XX:ParallelGCThreads=8 -XX:ConcGCThreads=8 -XX:G1ReservePercent=10 -XX:G1HeapRegionSize=4m"
LOGS_OPTS="-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/home/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=128M -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark"

if [[ "${DEBUG}" = "true" ]]
then
JVM_OPTS="${JVM_OPTS} -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
fi

spark-submit \
    --master spark://$(hostname -i):7077 \
    --deploy-mode client \
    --driver-cores 1 \
    --driver-memory ${DRIVER_MEMORY} \
    --num-executors ${EXEC} \
    --executor-cores ${EXEC_CORES} \
    --executor-memory ${EXEC_MEMORY} \
    --conf spark.executor.extraJavaOptions="${GC_OPTS} ${LOGS_OPTS}" \
    --class edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver \
    --conf spark.default.parallelism=$(echo "$((${EXEC_CORES} * ${EXEC}))") \
    --conf spark.eventLog.enabled=true \
    --conf spark.eventLog.dir=/tmp/data/logs \
    --conf spark.memory.offHeap.size=500mb \
    --driver-java-options "-Dproblem.size=${PROBLEM_SIZE} -Dproblem.steps=${PROBLEM_STEPS} -Dsparklogs=/tmp/data ${JVM_OPTS}" \
    ${@} \
    iga-adi-graphx-assembly-0.1.0.jar #&> "${EXEC}-nodes-48-${EXEC_CORES}-${RUN}.txt"



#    --conf spark.shuffle.file.buffer=128k \
#    --conf spark.reducer.maxSizeInFlight=128k \
#    --conf spark.memory.useLegacyMode=true \
#    --conf spark.shuffle.memoryFraction=0.4 \
#    --conf spark.storage.memoryFraction=0.4 \
#    --conf spark.shuffle.consolidateFiles=true \
#    --conf spark.reducer.maxMbInFlight=192 \
#    --conf spark.cleaner.referenceTracking=false \
#    --conf spark.files.useFetchCache=false \