@echo off
echo VodouLauncher Build Script for Windows
echo ======================================

echo.
echo 1. Make sure Android Studio is installed
echo 2. Make sure Java JDK 17 is installed
echo 3. Make sure Android SDK is installed
echo.

echo Checking prerequisites...

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Java not found in PATH
    echo Please install Java JDK 17
    pause
    exit /b 1
)

where gradlew.bat >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: gradlew.bat not found in current directory
    echo Please run this script from the project root
    pause
    exit /b 1
)

echo.
echo Select build type:
echo 1. Debug APK (for testing)
echo 2. Release APK (for distribution)
echo 3. Run on connected device
echo 4. Clean project
echo.

set /p choice="Enter choice (1-4): "

if "%choice%"=="1" (
    echo Building Debug APK...
    call gradlew assembleDebug
    if %errorlevel% equ 0 (
        echo.
        echo SUCCESS: Debug APK built!
        echo Location: app\build\outputs\apk\debug\app-debug.apk
    )
) else if "%choice%"=="2" (
    echo Building Release APK...
    echo Note: Release builds require signing configuration
    call gradlew assembleRelease
    if %errorlevel% equ 0 (
        echo.
        echo SUCCESS: Release APK built!
        echo Location: app\build\outputs\apk\release\app-release.apk
    )
) else if "%choice%"=="3" (
    echo Installing on connected device...
    call gradlew installDebug
    if %errorlevel% equ 0 (
        echo.
        echo SUCCESS: App installed on device!
    )
) else if "%choice%"=="4" (
    echo Cleaning project...
    call gradlew clean
    echo Project cleaned!
) else (
    echo Invalid choice
)

echo.
echo Done!
pause