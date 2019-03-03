#!/usr/bin/env bash
sbt assembly
docker build . -t kbhit/iga-adi-pregel
docker push kbhit/iga-adi-pregel
