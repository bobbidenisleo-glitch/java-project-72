#!/bin/bash
COVERAGE=$(grep -oP 'Total.*?([0-9]+\%)' app/build/reports/jacoco/test/html/index.html | grep -oP '[0-9]+')
echo "{\"schemaVersion\":1,\"label\":\"coverage\",\"message\":\"${COVERAGE}%\",\"color\":\"brightgreen\"}" > .github/badges/jacoco.json
