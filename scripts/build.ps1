# Builds ONE executable JAR containing the API and the Vue UI:
#   backend\target\learning-tracker.jar
# Requirements: JDK 21+, Maven 3.9+, Node 20+.
$ErrorActionPreference = 'Stop'
Set-Location (Join-Path $PSScriptRoot '..')

Write-Host '==> Building frontend'
Push-Location frontend
npm ci; if ($LASTEXITCODE) { throw 'npm ci failed' }
npm run build; if ($LASTEXITCODE) { throw 'frontend build failed' }
Pop-Location

Write-Host '==> Building backend (with UI bundled)'
Push-Location backend
mvn -B -Pui clean package; if ($LASTEXITCODE) { throw 'maven build failed' }
Pop-Location

Write-Host '==> Done: backend\target\learning-tracker.jar'
