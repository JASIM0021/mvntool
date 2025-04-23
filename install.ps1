<#
.SYNOPSIS
  Installs mvntool CLI on Windows systems
.DESCRIPTION
  Downloads the mvntool JAR and creates a launcher script
#>

$REPO_URL = "https://github.com/JASIM0021/mvntool"
$INSTALL_DIR = "$env:USERPROFILE\.mvntool"
$BIN_DIR = "$env:USERPROFILE\AppData\Local\mvntool\bin"
$JAR_NAME = "mvntool.jar"
$SCRIPT_NAME = "mvntool.bat"

Write-Host "📦 Installing mvntool..." -ForegroundColor Cyan

# Create directories if they don't exist
if (-not (Test-Path $INSTALL_DIR)) {
    New-Item -ItemType Directory -Path $INSTALL_DIR -Force | Out-Null
}

if (-not (Test-Path $BIN_DIR)) {
    New-Item -ItemType Directory -Path $BIN_DIR -Force | Out-Null
}

# Download the JAR file
Write-Host "⬇️ Downloading latest version..." -ForegroundColor Cyan
try {
    $ProgressPreference = 'SilentlyContinue' # Speeds up downloads
    Invoke-WebRequest -Uri "$REPO_URL/releases/download/v.1.0.0/mvntool-1.0.0.jar" -OutFile "$INSTALL_DIR\$JAR_NAME"
    
    # Verify download
    if (-not (Test-Path "$INSTALL_DIR\$JAR_NAME")) {
        throw "Download failed"
    }
}
catch {
    Write-Host "❌ Failed to download mvntool: $_" -ForegroundColor Red
    exit 1
}

# Create batch file
Write-Host "📝 Creating launcher script..." -ForegroundColor Cyan
@"
@echo off
java -jar "$INSTALL_DIR\$JAR_NAME" %*
"@ | Out-File -Encoding ASCII -FilePath "$BIN_DIR\$SCRIPT_NAME"

# Add to PATH if not already present
$currentPath = [Environment]::GetEnvironmentVariable("PATH", "User")
if (-not $currentPath.Contains($BIN_DIR)) {
    Write-Host "🔧 Adding to PATH..." -ForegroundColor Cyan
    [Environment]::SetEnvironmentVariable(
        "PATH",
        "$currentPath;$BIN_DIR",
        "User"
    )
    $env:PATH += ";$BIN_DIR"
}

Write-Host "🎉 Installation complete!" -ForegroundColor Green
Write-Host "Try: mvntool --help" -ForegroundColor Green
Write-Host "Note: You may need to restart your terminal for changes to take effect." -ForegroundColor Yellow