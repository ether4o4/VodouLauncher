#!/data/data/com.termux/files/usr/bin/bash

# 4GAuteauOS Simple Termux Builder
# Minimal setup for building on Termux

echo "🚀 4GAuteauOS Termux Builder (Simple)"
echo "====================================="
echo "Building directly on your Android device"
echo ""

# Check Termux
if [ ! -d "/data/data/com.termux/files/usr" ]; then
    echo "❌ Run this in Termux on Android"
    echo "   Install Termux from F-Droid"
    exit 1
fi

# Update Termux
echo "📦 Updating Termux packages..."
pkg update -y && pkg upgrade -y

# Install minimal requirements
echo "📦 Installing build tools..."
pkg install -y openjdk-17 git wget unzip

# Check storage
echo "💾 Checking storage..."
df -h /data

# Clone repository
echo "📥 Getting 4GAuteauOS code..."
if [ -d "4GAuteauOS" ]; then
    cd 4GAuteauOS
    git pull
else
    git clone https://github.com/ether4o4/4GAuteauOS.git
    cd 4GAuteauOS
fi

# Make gradlew executable
chmod +x gradlew

# Try to build with existing setup
echo "🔨 Attempting to build..."
echo "   This will take a few minutes..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Find APK
    APK_FILE=$(find . -name "*.apk" -type f | head -1)
    if [ -f "$APK_FILE" ]; then
        APK_NAME=$(basename "$APK_FILE")
        
        # Copy to Downloads
        cp "$APK_FILE" /sdcard/Download/4gauteauos-debug.apk 2>/dev/null || \
        cp "$APK_FILE" ~/storage/downloads/4gauteauos-debug.apk 2>/dev/null || \
        echo "APK is at: $APK_FILE"
        
        echo ""
        echo "🎉 4GAUTEAUOS APK READY!"
        echo "========================"
        echo "📱 File: $APK_NAME"
        echo ""
        echo "📱 To install:"
        echo "1. Open your file manager"
        echo "2. Find the APK in Downloads"
        echo "3. Tap to install"
        echo "4. Enable 'Install from unknown sources' if needed"
        echo "5. Set as default launcher"
        echo ""
        echo "💡 Quick install from Termux:"
        echo "   termux-open /sdcard/Download/4gauteauos-debug.apk"
    fi
else
    echo "❌ Build failed"
    echo ""
    echo "💡 Alternative: Use pre-built APK"
    echo "1. Download from: https://github.com/ether4o4/4GAuteauOS"
    echo "2. Check 'Releases' section"
    echo "3. Or build on a computer and transfer APK"
fi