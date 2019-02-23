#!/usr/bin/env bash
helm install stable/influxdb --name influxdb --namespace monitoring --set setDefaultUser.user.password=admin
