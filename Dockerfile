FROM bde2020/spark-submit:2.4.0-hadoop2.7

ARG JAR_NAME=iga-adi-pregel.jar
ARG APP_HOME=/usr/src/app

ENV SPARK_APPLICATION_JAR_LOCATION $APP_HOME/$JAR_NAME
ENV SPARK_APPLICATION_MAIN_CLASS edu.agh.kboom.iga.adi.graph.IgaAdiPregelSolver

RUN mkdir -p $APP_HOME
COPY target/scala-2.11/iga-adi-graphx-assembly*.jar $SPARK_APPLICATION_JAR_LOCATION

