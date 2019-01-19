FROM kbhit/spark

ARG JAR_NAME=iga-adi-pregel.jar

COPY target/scala-2.11/iga-adi-graphx-assembly*.jar /opt/$JAR_NAME

