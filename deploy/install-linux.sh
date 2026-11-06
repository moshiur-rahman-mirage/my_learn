#!/usr/bin/env bash
# Installs or updates the Learning Tracker as a systemd service.
#
#   sudo ./deploy/install-linux.sh [path/to/learning-tracker.jar]
#
# Default JAR: backend/target/learning-tracker.jar (build it first with scripts/build.sh).
# Safe to re-run for updates: data in /var/lib/learning-tracker is never touched,
# except that a backup copy of the database is taken before the new version starts.
set -euo pipefail

REPO="$(cd "$(dirname "$0")/.." && pwd)"
JAR="${1:-$REPO/backend/target/learning-tracker.jar}"
APP_DIR=/opt/learning-tracker
DATA_DIR=/var/lib/learning-tracker
UNIT=/etc/systemd/system/learning-tracker.service

[[ $EUID -eq 0 ]] || { echo "Run with sudo." >&2; exit 1; }
[[ -f "$JAR" ]] || { echo "JAR not found: $JAR (run scripts/build.sh first)" >&2; exit 1; }
command -v java >/dev/null || { echo "Java 21+ is required (e.g. apt install openjdk-21-jre-headless)." >&2; exit 1; }
JAVA_MAJOR=$(java -XshowSettings:properties -version 2>&1 | awk -F'= ' '/java.specification.version/ {print $2}')
(( ${JAVA_MAJOR%%.*} >= 21 )) || { echo "Java 21+ is required, found $JAVA_MAJOR" >&2; exit 1; }

id learntracker >/dev/null 2>&1 || useradd --system --no-create-home --shell /usr/sbin/nologin learntracker

if systemctl is-active --quiet learning-tracker; then
  echo "==> Stopping running service"
  systemctl stop learning-tracker
fi

# Pre-update safety copy (the service is stopped, so a plain copy is consistent).
if [[ -f "$DATA_DIR/learning-tracker.db" ]]; then
  mkdir -p "$DATA_DIR/backups"
  cp "$DATA_DIR/learning-tracker.db" "$DATA_DIR/backups/pre-update-$(date +%Y%m%d-%H%M%S).db"
  chown -R learntracker:learntracker "$DATA_DIR/backups"
  echo "==> Database copied to $DATA_DIR/backups/"
fi

echo "==> Installing JAR to $APP_DIR"
install -d -m 755 "$APP_DIR"
install -m 644 "$JAR" "$APP_DIR/learning-tracker.jar"

echo "==> Installing systemd unit"
install -m 644 "$REPO/deploy/systemd/learning-tracker.service" "$UNIT"
systemctl daemon-reload
systemctl enable --now learning-tracker

PORT=$(grep -h '^SERVER_PORT=' /etc/learning-tracker.env 2>/dev/null | cut -d= -f2 || true)
PORT=${PORT:-8080}
echo -n "==> Waiting for the app"
for _ in $(seq 1 60); do
  if curl -sf "http://127.0.0.1:$PORT/actuator/health" >/dev/null; then
    echo " - up at http://127.0.0.1:$PORT"
    exit 0
  fi
  echo -n "."; sleep 1
done
echo; echo "Not healthy yet. Check: journalctl -u learning-tracker -n 100" >&2
exit 1
