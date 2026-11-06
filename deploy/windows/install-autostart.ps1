# Native install on Windows: runs the Learning Tracker at log-on (no window), via Task Scheduler.
#
#   powershell -ExecutionPolicy Bypass -File deploy\windows\install-autostart.ps1 [-Port 8080] [-Jar path]
#
# Installs to %LOCALAPPDATA%\LearningTracker\app, stores data in %LOCALAPPDATA%\LearningTracker\data.
# Re-run to update (data is kept; a copy of the database is made first).
# Remove with: Unregister-ScheduledTask -TaskName LearningTracker -Confirm:$false
param(
    [int]$Port = 8080,
    [string]$Jar = (Join-Path $PSScriptRoot '..\..\backend\target\learning-tracker.jar')
)
$ErrorActionPreference = 'Stop'

$root = Join-Path $env:LOCALAPPDATA 'LearningTracker'
$appDir = Join-Path $root 'app'
$dataDir = Join-Path $root 'data'
$taskName = 'LearningTracker'

if (-not (Test-Path $Jar)) { throw "JAR not found: $Jar (run scripts\build.ps1 first)" }
$javaw = (Get-Command javaw.exe -ErrorAction SilentlyContinue).Source
if (-not $javaw) { throw 'javaw.exe not found. Install a JDK/JRE 21+ (e.g. Eclipse Temurin) and add it to PATH.' }

# Stop a running instance before replacing the JAR
Stop-ScheduledTask -TaskName $taskName -ErrorAction SilentlyContinue
Get-CimInstance Win32_Process -Filter "Name='javaw.exe'" |
    Where-Object { $_.CommandLine -like '*LearningTracker*learning-tracker.jar*' } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
Start-Sleep -Seconds 2

New-Item -ItemType Directory -Force $appDir, $dataDir | Out-Null
$db = Join-Path $dataDir 'learning-tracker.db'
if (Test-Path $db) {
    $backups = Join-Path $dataDir 'backups'
    New-Item -ItemType Directory -Force $backups | Out-Null
    Copy-Item $db (Join-Path $backups ("pre-update-{0:yyyyMMdd-HHmmss}.db" -f (Get-Date)))
    Write-Host "==> Database copied to $backups"
}
Copy-Item $Jar (Join-Path $appDir 'learning-tracker.jar') -Force

$javaArgs = "-Xmx256m -jar `"$appDir\learning-tracker.jar`" --server.port=$Port --server.address=127.0.0.1 --app.data-dir=`"$dataDir`""
$action = New-ScheduledTaskAction -Execute $javaw -Argument $javaArgs -WorkingDirectory $appDir
$trigger = New-ScheduledTaskTrigger -AtLogOn -User $env:USERNAME
$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries `
    -ExecutionTimeLimit ([TimeSpan]::Zero) -RestartCount 3 -RestartInterval (New-TimeSpan -Minutes 1)
Register-ScheduledTask -TaskName $taskName -Action $action -Trigger $trigger -Settings $settings `
    -Description 'Personal Learning Tracker (local web app)' -Force | Out-Null
Start-ScheduledTask -TaskName $taskName

Write-Host -NoNewline '==> Waiting for the app'
for ($i = 0; $i -lt 60; $i++) {
    try {
        Invoke-RestMethod "http://127.0.0.1:$Port/actuator/health" -TimeoutSec 2 | Out-Null
        Write-Host " - up at http://127.0.0.1:$Port (starts automatically at log-on)"
        exit 0
    } catch { Write-Host -NoNewline '.'; Start-Sleep -Seconds 1 }
}
Write-Warning "Not healthy yet. Is port $Port free? Try: java -jar `"$appDir\learning-tracker.jar`" --server.port=$Port"
exit 1
