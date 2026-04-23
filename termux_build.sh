#!/data/data/com.termux/files/usr/bin/bash

# 4GAuteauOS Termux Build Script
# Build the APK directly on your Android device using Termux

echo "🚀 4GAuteauOS Termux Builder"
echo "============================="
echo "Building Vista-evolved Android launcher on your phone!"
echo ""

# Check if we're in Termux
if [ ! -d "/data/data/com.termux/files/usr" ]; then
    echo "❌ This script must be run in Termux on Android"
    echo "   Install Termux from: https://f-droid.org/packages/com.termux/"
    exit 1
fi

# Update and install dependencies
echo "📦 Installing dependencies..."
pkg update -y
pkg install -y openjdk-17 gradle wget unzip git

# Check Java
echo "☕ Checking Java..."
java -version

# Create build directory
BUILD_DIR="$HOME/4gauteauos-build"
echo "📁 Setting up build directory: $BUILD_DIR"
mkdir -p "$BUILD_DIR"
cd "$BUILD_DIR"

# Clone or update repository
if [ -d "4GAuteauOS" ]; then
    echo "📥 Updating existing repository..."
    cd 4GAuteauOS
    git pull origin main
else
    echo "📥 Cloning 4GAuteauOS repository..."
    git clone https://github.com/ether4o4/4GAuteauOS.git
    cd 4GAuteauOS
fi

echo "✅ Repository ready"

# Make gradlew executable
chmod +x ./gradlew

# Set up Android SDK for Termux
echo "🤖 Setting up Android SDK for Termux..."
ANDROID_SDK="$HOME/android-sdk"
if [ ! -d "$ANDROID_SDK" ]; then
    echo "📥 Downloading Android Command Line Tools..."
    mkdir -p "$ANDROID_SDK"
    cd "$ANDROID_SDK"
    
    # Download command line tools
    wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
    unzip commandlinetools-linux-9477386_latest.zip
    rm commandlinetools-linux-9477386_latest.zip
    
    # Set up SDK
    mkdir -p cmdline-tools/latest
    mv cmdline-tools/* cmdline-tools/latest/
    
    # Accept licenses
    yes | cmdline-tools/latest/bin/sdkmanager --licenses
    
    echo "✅ Android SDK installed"
fi

# Set environment variables
export ANDROID_HOME="$ANDROID_SDK"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

echo "🔧 Environment:"
echo "   ANDROID_HOME: $ANDROID_HOME"
echo "   JAVA_HOME: $(dirname $(dirname $(readlink -f $(which java))))"

# Go back to project
cd "$BUILD_DIR/4GAuteauOS"

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build the APK
echo "🔨 Building 4GAuteauOS APK..."
echo "   This may take 5-10 minutes on your phone..."
./gradlew assembleDebug

# Check result
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Find the APK
    APK_FILE=$(find app/build/outputs/apk -name "*.apk" -type f | head -1)
    if [ -f "$APK_FILE" ]; then
        APK_NAME=$(basename "$APK_FILE")
        APK_SIZE=$(stat -c%s "$APK_FILE")
        APK_SIZE_MB=$(echo "scale=2; $APK_SIZE / 1024 / 1024" | bc)
        
        # Copy to downloads for easy access
        cp "$APK_FILE" "/sdcard/Download/4gauteauos-debug.apk"
        
        echo ""
        echo "🎉 4GAUTEAUOS APK READY!"
        echo "========================"
        echo "📱 APK: $APK_NAME"
        echo "📦 Size: ${APK_SIZE_MB}MB"
        echo "📁 Location in Termux: $APK_FILE"
        echo "📁 Location in Downloads: /sdcard/Download/4gauteauos-debug.apk"
        echo ""
        echo "📱 Installation:"
        echo "1. Open Files app on your phone"
        echo "2. Go to Downloads folder"
        echo "3. Tap on '4gauteauos-debug.apk'"
        echo "4. Install (enable 'Install from unknown sources' if needed)"
        echo "5. Set as default launcher in Android settings"
        echo ""
        echo "✨ Features: Vista-evolved glass UI, performance optimized"
        echo "========================"
        
        # Also show how to access from Termux
        echo ""
        echo "💡 Quick access from Termux:"
        echo "   termux-open /sdcard/Download/4gauteauos-debug.apk"
    else
        echo "❌ APK file not found"
        echo "   Check: find . -name '*.apk' -type f"
    fi
else
    echo "❌ Build failed"
    echo "   Check error messages above"
    echo ""
    echo "💡 Troubleshooting:"
    echo "1. Make sure you have enough storage (500MB free)"
    echo "2. Try: pkg upgrade"
    echo "3. Check internet connection"
    exit 1
fi