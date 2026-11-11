#!/bin/bash
# Runs the daily lifer map export and pushes the new map URL to ornitho-service.
set -e
cd "$(dirname "$0")"
mvn -q compile exec:java -Dexec.mainClass="com.nirocca.ornithoalert.LiferMapMain"
