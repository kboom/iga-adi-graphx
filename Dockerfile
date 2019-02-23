FROM kbhit/spark

COPY monitoring/metrics.properties /opt/metrics.properties
COPY monitoring/metrics-influxdb.jar /opt/metrics-influxdb.jar
COPY monitoring/spark-influx-sink.jar /opt/spark-influx-sink.jar
COPY target/scala-2.11/iga-adi-graphx-assembly*.jar /opt/iga-adi-pregel.jar

