#!/bin/bash

# VodouLauncher Local Build Script
# Run this on a machine with Android Studio or Android SDK installed

echo "🚀 Building VodouLauncher APK locally..."
echo "========================================"

# Check for Java
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java JDK 17 or higher."
    echo "   Download: https://adoptium.net/temurin/releases/"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version $JAVA_VERSION is too old. Please install Java JDK 17 or higher."
    exit 1
fi
echo "✅ Java $JAVA_VERSION found"

# Check for Android SDK
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_HOME="$HOME/Android/Sdk"
    elif [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    else
        echo "⚠️ ANDROID_HOME not set. Trying to find Android SDK..."
        # Try to find sdkmanager
        if command -v sdkmanager &> /dev/null; then
            echo "✅ Android SDK tools found via sdkmanager"
        else
            echo "❌ Android SDK not found. Please:"
            echo "   1. Install Android Studio: https://developer.android.com/studio"
            echo "   2. OR set ANDROID_HOME environment variable"
            echo "   3. OR install Android Command Line Tools"
            exit 1
        fi
    fi
fi

echo "✅ Android SDK: $ANDROID_HOME"

# Make gradlew executable
chmod +x ./gradlew

# Build the APK
echo "🔨 Building APK..."
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Find the APK
    APK_FILE=$(find app/build/outputs/apk -name "*.apk" -type f | head -1)
    if [ -f "$APK_FILE" ]; then
        APK_NAME=$(basename "$APK_FILE")
        APK_SIZE=$(stat -f%z "$APK_FILE" 2>/dev/null || stat -c%s "$APK_FILE")
        APK_SIZE_MB=$(echo "scale=2; $APK_SIZE / 1024 / 1024" | bc)
        
        echo ""
        echo "🎉 VODOU LAUNCHER APK READY!"
        echo "============================="
        echo "📱 APK: $APK_NAME"
        echo "📦 Size: ${APK_SIZE_MB}MB"
        echo "📁 Location: $APK_FILE"
        echo ""
        echo "📱 Installation:"
        echo "1. Transfer APK to Android device"
        echo "2. Enable 'Install from unknown sources'"
        echo "3. Open APK file and install"
        echo "4. Set as default launcher in Android settings"
        echo ""
        echo "✨ Features: Vista-evolved glass UI, performance optimized"
        echo "============================="
        
        # Copy to current directory for easy access
        cp "$APK_FILE" "./vodoulauncher-debug.apk"
        echo "📥 Also copied to: ./vodoulauncher-debug.apk"
    else
        echo "❌ APK file not found in build output"
        echo "   Check app/build/outputs/apk/ directory"
    fi
else
    echo "❌ Build failed. Check the error messages above."
    exit 1
fi