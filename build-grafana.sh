#!/usr/bin/env bash
docker build -t kbhit/grafana \
  --build-arg "GRAFANA_VERSION=5.3.1" \
  --build-arg "GF_INSTALL_PLUGINS=grafana-piechart-panel,snuids-radar-panel,vonage-status-panel,yesoreyeram-boomtable-panel,digrich-bubblechart-panel,petrslavotinek-carpetplot-panel,jdbranham-diagram-panel,savantly-heatmap-panel,michaeldmoore-multistat-panel,natel-plotly-panel,grafana-polystat-panel" .