#!/usr/bin/env bash
# Builds the release APK, copies it to dist/, and installs it on the phone over wireless adb if the
# phone can be found. Run from Git Bash:  bash mobile/scripts/ship-android.sh
# (After `expo prebuild`, android/local.properties needs sdk.dir and cmake.dir=…/cmake/3.31.6;
#  see docs/ANDROID_PROGRESS.md "Windows build gotcha".)
set -u
export MSYS_NO_PATHCONV=1
HERE="$(cd "$(dirname "$0")/.." && pwd)"
SDK="${ANDROID_HOME:-$LOCALAPPDATA/Android/Sdk}"
ADB="$SDK/platform-tools/adb.exe"
APK="$HERE/android/app/build/outputs/apk/release/app-release.apk"
DIST="$HERE/../dist/Learning Tracker-android.apk"

# Gradle 9 needs JDK 17 or 21 (not 25). Windows-style paths: MSYS_NO_PATHCONV stops Git Bash converting them.
export JAVA_HOME="${JAVA_HOME_21:-C:/Program Files/Java/jdk-21.0.11}"
export ANDROID_HOME="$(cygpath -m "$SDK")"
export PATH="$(cygpath -u "$JAVA_HOME")/bin:$PATH"

echo "==> Building release APK"
( cd "$HERE/android" && ./gradlew.bat assembleRelease -PreactNativeArchitectures=arm64-v8a --console=plain -q ) || { echo "BUILD FAILED"; exit 1; }
cp "$APK" "$DIST" && echo "==> APK: $DIST ($(du -h "$DIST" | cut -f1))"

phone() { "$ADB" devices | grep -m1 -E "^[0-9.]+:[0-9]+[[:space:]]+device$" | cut -f1; }
D="$(phone)"
if [ -z "$D" ]; then
  "$ADB" kill-server >/dev/null 2>&1; "$ADB" start-server >/dev/null 2>&1; sleep 3
  for p in $("$ADB" mdns services | grep connect | grep -oE '[0-9.]+:[0-9]+' | sort -u); do "$ADB" connect "$p" >/dev/null 2>&1; done
  D="$(phone)"
fi
if [ -z "$D" ]; then
  echo "==> Phone not reachable over wireless adb: skipped install (APK is in dist/)."
  exit 0
fi
echo "==> Installing on $D"
"$ADB" -s "$D" install -r "$(cygpath -w "$DIST")" | tail -1
