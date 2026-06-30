@echo off
title Luxe Dine Restaurant Billing System
echo =========================================
echo    Compiling Restaurant Billing System
echo =========================================

rem Ensure bin directory exists
if not exist bin (
    mkdir bin
)

rem Compile using specific Java 21 JDK
"C:\Users\laksh\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64\bin\javac.exe" -d bin -cp "lib/*" src/restaurant/model/*.java src/restaurant/database/*.java src/restaurant/ui/*.java src/restaurant/*.java

if %errorlevel% neq 0 (
    echo.
    echo [-] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo.
echo [+] Compilation successful!
echo Starting Luxe Dine System...
echo -----------------------------------------

rem Run application
"C:\Users\laksh\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64\bin\java.exe" -cp "bin;lib/*" restaurant.Main

if %errorlevel% neq 0 (
    echo.
    echo [-] Application terminated with exit code %errorlevel%
    pause
)
