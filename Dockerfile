FROM kbhit/spark

COPY monitoring/* /opt/
COPY target/scala-2.11/iga-adi-graphx-assembly*.jar /opt/iga-adi-pregel.jar

