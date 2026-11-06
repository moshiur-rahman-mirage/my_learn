<#
Installs the Learning Tracker desktop app for the current user WITHOUT the .exe installer.
Use this when Windows Smart App Control blocks the unsigned installer: the app itself
(the launcher plus a Java runtime signed by the Eclipse Foundation) is not affected.

  powershell -ExecutionPolicy Bypass -File deploy\windows\install-app.ps1              # install / update
  powershell -ExecutionPolicy Bypass -File deploy\windows\install-app.ps1 -Uninstall   # remove (data is kept)

What it does:
  - copies dist\Learning Tracker\  ->  %LOCALAPPDATA%\Learning Tracker\  (stops the running app first)
  - Start-menu shortcut (and a desktop shortcut unless -NoDesktopShortcut)
  - an entry under Settings > Apps > Installed apps, so it can be uninstalled from there too
Your data stays in %LOCALAPPDATA%\LearningTracker\data and is never touched by this script.
#>
param(
    [string]$Source = (Join-Path $PSScriptRoot '..\..\dist\Learning Tracker'),
    # same folder the .exe installer uses, so this updates an existing install instead of adding a copy
    [string]$InstallDir = (Join-Path $env:LOCALAPPDATA 'Learning Tracker'),
    [switch]$NoDesktopShortcut,
    [switch]$Uninstall,
    [switch]$NoStart,
    # for testing: where shortcuts go, and whether to touch the registry
    [string]$StartMenuDir = (Join-Path $env:APPDATA 'Microsoft\Windows\Start Menu\Programs'),
    [string]$DesktopDir = [Environment]::GetFolderPath('Desktop'),
    [switch]$NoRegistry
)
$ErrorActionPreference = 'Stop'
$AppName = 'Learning Tracker'
$Exe = Join-Path $InstallDir "$AppName.exe"
$UninstallKey = 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Uninstall\LearningTracker'
$StartLink = Join-Path $StartMenuDir "$AppName.lnk"
$DesktopLink = Join-Path $DesktopDir "$AppName.lnk"

function Stop-RunningApp {
    $running = Get-Process -ErrorAction SilentlyContinue | Where-Object { $_.Path -and $_.Path.StartsWith($InstallDir, [StringComparison]::OrdinalIgnoreCase) }
    if ($running) {
        Write-Host 'Stopping the running app...'
        $running | Stop-Process -Force
        Start-Sleep -Seconds 2
    }
}

function New-Shortcut([string]$path, [string]$target) {
    New-Item -ItemType Directory -Force (Split-Path $path) | Out-Null
    $shell = New-Object -ComObject WScript.Shell
    $link = $shell.CreateShortcut($path)
    $link.TargetPath = $target
    $link.WorkingDirectory = Split-Path $target
    $link.IconLocation = "$target,0"
    $link.Description = 'Personal Learning Tracker'
    $link.Save()
}

if ($Uninstall) {
    Stop-RunningApp
    Remove-Item -Force $StartLink, $DesktopLink -ErrorAction SilentlyContinue
    if (-not $NoRegistry) {
        Remove-Item $UninstallKey -ErrorAction SilentlyContinue
        Remove-ItemProperty 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Run' -Name 'LearningTracker' -ErrorAction SilentlyContinue
    }
    if (Test-Path $InstallDir) { Remove-Item -Recurse -Force $InstallDir }
    Write-Host "Uninstalled. Your data is still in $env:LOCALAPPDATA\LearningTracker\data"
    return
}

$Source = (Resolve-Path $Source -ErrorAction SilentlyContinue).Path
if (-not $Source -or -not (Test-Path (Join-Path $Source "$AppName.exe"))) {
    throw "App folder not found. Build it first:  .\scripts\build-installer.ps1 -AppImageOnly"
}

Stop-RunningApp
if (Test-Path $InstallDir) { Remove-Item -Recurse -Force $InstallDir } # replace old version cleanly
New-Item -ItemType Directory -Force $InstallDir | Out-Null
Copy-Item -Recurse -Force (Join-Path $Source '*') $InstallDir
Write-Host "Installed to $InstallDir"

New-Shortcut $StartLink $Exe
if (-not $NoDesktopShortcut) { New-Shortcut $DesktopLink $Exe }
Write-Host 'Shortcuts created.'

if (-not $NoRegistry) {
    $self = Join-Path $InstallDir 'uninstall.ps1'
    Copy-Item -Force $PSCommandPath $self
    $version = (Get-Item $Exe).VersionInfo.ProductVersion
    New-Item -Force $UninstallKey | Out-Null
    $values = @{
        DisplayName     = $AppName
        DisplayVersion  = $version
        Publisher       = 'mylearn'
        DisplayIcon     = $Exe
        InstallLocation = $InstallDir
        UninstallString = "powershell.exe -NoProfile -ExecutionPolicy Bypass -File `"$self`" -Uninstall"
        NoModify        = 1
        NoRepair        = 1
    }
    foreach ($k in $values.Keys) { Set-ItemProperty $UninstallKey -Name $k -Value $values[$k] }
    $size = [int]((Get-ChildItem -Recurse $InstallDir | Measure-Object Length -Sum).Sum / 1KB)
    Set-ItemProperty $UninstallKey -Name EstimatedSize -Value $size -Type DWord
}

if (-not $NoStart) {
    Start-Process $Exe
    Write-Host 'Starting Learning Tracker (it opens in your browser; look for the tray icon by the clock).'
}
