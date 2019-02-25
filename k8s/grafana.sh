#!/usr/bin/env bash
helm install --name grafana stable/grafana \
    --set sidecar.datasources.enabled=true \
    --set plugins="grafana-piechart-panel,snuids-radar-panel,vonage-status-panel,yesoreyeram-boomtable-panel,digrich-bubblechart-panel,petrslavotinek-carpetplot-panel,jdbranham-diagram-panel,savantly-heatmap-panel,michaeldmoore-multistat-panel,natel-plotly-panel,grafana-polystat-panel"
