#!/usr/bin/env bash
# Builds ONE executable JAR containing the API and the Vue UI:
#   backend/target/learning-tracker.jar
# Requirements: JDK 21+, Maven 3.9+, Node 20+.
set -euo pipefail
cd "$(dirname "$0")/.."

echo "==> Building frontend"
(cd frontend && npm ci && npm run build)

echo "==> Building backend (with UI bundled)"
(cd backend && mvn -B -Pui clean package)

echo "==> Done: backend/target/learning-tracker.jar"
