<#
Builds the installable Windows app (bundles its own Java; the target PC needs nothing else):

  dist\Learning Tracker\Learning Tracker.exe     portable app folder (always)
  dist\Learning Tracker-<version>.exe            installer (needs WiX Toolset 3.14)

Usage:
  .\scripts\build-installer.ps1                  full build (UI + JAR + app + installer)
  .\scripts\build-installer.ps1 -SkipBuild       reuse backend\target\learning-tracker.jar
  .\scripts\build-installer.ps1 -AppImageOnly    skip the installer (no WiX needed)
  .\scripts\build-installer.ps1 -Port 40000      bake in another port (default 38427)

Requirements: JDK 21+ (contains jpackage), Maven 3.9+, Node 20+.
WiX 3.14, once, in an *administrator* PowerShell:  winget install WiXToolset.WiXToolset
#>
param(
    [switch]$SkipBuild,
    [switch]$AppImageOnly,
    [int]$Port = 38427
)
$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Set-Location $root

$AppName = 'Learning Tracker'
# Never change: it lets a newer installer replace an older version (data is kept).
$UpgradeUuid = 'e16bc7a9-93c4-44a7-b5a7-1cdba7c3b5d5'
# Java modules for the bundled runtime (keeps it ~60 MB instead of the full JDK).
$Modules = @(
    'java.base', 'java.compiler', 'java.desktop', 'java.instrument', 'java.logging', 'java.management',
    'java.naming', 'java.net.http', 'java.prefs', 'java.rmi', 'java.scripting', 'java.security.jgss',
    'java.sql', 'java.transaction.xa', 'java.xml', 'jdk.charsets', 'jdk.crypto.cryptoki',
    'jdk.management', 'jdk.naming.dns', 'jdk.unsupported', 'jdk.zipfs'
) -join ','

function Find-Tool([string]$name) {
    $cmd = Get-Command $name -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\$name.exe")) { return "$env:JAVA_HOME\bin\$name.exe" }
    return $null
}

function Add-WixToPath {
    if (Get-Command candle.exe -ErrorAction SilentlyContinue) { return $true }
    if (Get-Command wix.exe -ErrorAction SilentlyContinue) { return $true }
    $candidates = @()
    if ($env:WIX) { $candidates += (Join-Path $env:WIX 'bin') }
    $candidates += Get-ChildItem "${env:ProgramFiles(x86)}\WiX Toolset v3*\bin" -Directory -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending | ForEach-Object FullName
    $candidates += (Join-Path $root '.tools\wix314')
    foreach ($dir in $candidates) {
        if ($dir -and (Test-Path (Join-Path $dir 'candle.exe'))) {
            $env:PATH = "$dir;$env:PATH"
            return $true
        }
    }
    return $false
}

# 1. Executable JAR with the UI inside
if (-not $SkipBuild) { & (Join-Path $PSScriptRoot 'build.ps1') }
$jar = Join-Path $root 'backend\target\learning-tracker.jar'
if (-not (Test-Path $jar)) { throw "Not found: $jar (run without -SkipBuild)" }

$jpackage = Find-Tool 'jpackage'
if (-not $jpackage) { throw 'jpackage not found: install a JDK 21+ and put its bin folder on PATH (or set JAVA_HOME).' }

[xml]$pom = Get-Content (Join-Path $root 'backend\pom.xml')
$version = $pom.project.version -replace '-SNAPSHOT$', ''

# jpackage copies the whole --input folder, so stage just the JAR
$work = Join-Path $root 'backend\target\jpackage'
$dist = Join-Path $root 'dist'
Remove-Item -Recurse -Force $work -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force (Join-Path $work 'input'), $dist | Out-Null
Copy-Item $jar (Join-Path $work 'input')

# 2. App folder with launcher exe + trimmed Java runtime
$appImage = Join-Path $dist $AppName
Remove-Item -Recurse -Force $appImage -ErrorAction SilentlyContinue
Write-Host "==> Creating app folder (version $version, port $Port)"
& $jpackage --type app-image --dest $dist `
    --name $AppName --app-version $version --vendor 'mylearn' `
    --description 'Personal Learning Tracker' `
    --icon (Join-Path $root 'deploy\windows\app.ico') `
    --input (Join-Path $work 'input') --main-jar 'learning-tracker.jar' `
    --add-modules $Modules `
    --jlink-options '--strip-debug --no-man-pages --no-header-files --strip-native-commands --compress=zip-6' `
    --java-options '-Dapp.desktop=true' `
    --java-options "-Dserver.port=$Port" `
    --java-options '-Xmx256m' `
    --java-options '-XX:+UseSerialGC' `
    --java-options '-XX:TieredStopAtLevel=1'
if ($LASTEXITCODE) { throw 'jpackage (app-image) failed' }
Write-Host "==> App folder: $appImage"

if ($AppImageOnly) { return }

# 3. Installer
if (-not (Add-WixToPath)) {
    Write-Warning @"
WiX Toolset not found, so only the portable folder was built.
Install it once in an ADMINISTRATOR PowerShell, then run this script again with -SkipBuild:
    winget install WiXToolset.WiXToolset
"@
    return
}
$installer = Join-Path $dist "$AppName-$version.exe"
Remove-Item -Force $installer -ErrorAction SilentlyContinue
Write-Host '==> Creating installer'
& $jpackage --type exe --dest $dist `
    --app-image $appImage `
    --name $AppName --app-version $version --vendor 'mylearn' `
    --description 'Personal Learning Tracker' `
    --win-per-user-install --win-dir-chooser `
    --win-menu --win-menu-group $AppName `
    --win-shortcut --win-shortcut-prompt `
    --win-upgrade-uuid $UpgradeUuid
if ($LASTEXITCODE) { throw 'jpackage (installer) failed' }
Write-Host "==> Installer: $installer"
