# Restaurant Billing System Helper Script

# Ensure bin directory exists
if (!(Test-Path bin)) {
    New-Item -ItemType Directory -Path bin | Out-Null
}

Write-Host "=========================================" -ForegroundColor Gold
Write-Host "   Compiling Restaurant Billing System   " -ForegroundColor Gold
Write-Host "=========================================" -ForegroundColor Gold

# Compile using specific Java 21 JDK
& "C:\Users\laksh\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64\bin\javac.exe" -d bin -cp "lib/*" src/restaurant/model/*.java src/restaurant/database/*.java src/restaurant/ui/*.java src/restaurant/*.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "[-] Compilation failed!" -ForegroundColor Red
    Exit $LASTEXITCODE
}

Write-Host "[+] Compilation successful!" -ForegroundColor Green
Write-Host "Starting Luxe Dine System..." -ForegroundColor Green
Write-Host "-----------------------------------------" -ForegroundColor Gray

# Run with class path including bin/ and lib/*.jar
& "C:\Users\laksh\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64\bin\java.exe" -cp "bin;lib/*" restaurant.Main
