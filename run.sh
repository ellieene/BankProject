#!/bin/bash
set -e
docker compose build bank-db
docker compose up -d bank-db
sleep 10
mvn clean verify
docker compose build bank-project
docker compose up -d bank-project