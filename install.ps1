$REPO_URL = "https://github.com/JASIM0021/mvntool"
$INSTALL_DIR = "$env:USERPROFILE\.mvntool"
$BIN_DIR = "$env:USERPROFILE\AppData\Local\Microsoft\WindowsApps"
$JAR_NAME = "mvntool.jar"
$SCRIPT_NAME = "mvntool.bat"

Write-Host "📦 Installing mvntool..."

New-Item -ItemType Directory -Force -Path $INSTALL_DIR | Out-Null
New-Item -ItemType Directory -Force -Path $BIN_DIR | Out-Null

Write-Host "⬇️ Downloading latest version..."
Invoke-WebRequest -Uri "$REPO_URL/latest/mvntool.jar" -OutFile "$INSTALL_DIR\$JAR_NAME"

Write-Host "📝 Creating executable script..."
@"
@echo off
java -jar "$INSTALL_DIR\$JAR_NAME" %*
"@ | Out-File -Encoding ASCII -FilePath "$BIN_DIR\$SCRIPT_NAME"

$currentPath = [Environment]::GetEnvironmentVariable("PATH", "User")
if ($currentPath -notlike "*$BIN_DIR*") {
    [Environment]::SetEnvironmentVariable("PATH", "$currentPath;$BIN_DIR", "User")
    $env:PATH += ";$BIN_DIR"
}

Write-Host "🎉 Installation complete! Try: mvntool --help"