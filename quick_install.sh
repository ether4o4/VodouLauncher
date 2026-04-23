#!/data/data/com.termux/files/usr/bin/bash

# 4GAuteauOS Quick Install
# Simple download and install

echo "🚀 4GAuteauOS Quick Install"
echo "==========================="

# Set download URL
APK_URL="https://github.com/ether4o4/4GAuteauOS/releases/latest/download/4gauteauos-debug.apk"
APK_NAME="4gauteauos.apk"
DOWNLOAD_DIR="$HOME"

echo "📥 Downloading 4GAuteauOS..."
echo "From: $APK_URL"
echo "To: $DOWNLOAD_DIR/$APK_NAME"

# Download
curl -L -o "$DOWNLOAD_DIR/$APK_NAME" "$APK_URL"

# Check download
if [ $? -eq 0 ] && [ -f "$DOWNLOAD_DIR/$APK_NAME" ]; then
    FILE_SIZE=$(stat -c%s "$DOWNLOAD_DIR/$APK_NAME")
    if [ $FILE_SIZE -gt 10000 ]; then
        echo "✅ Download successful!"
        echo "📦 Size: $(($FILE_SIZE/1024/1024))MB"
        
        # Show location
        echo ""
        echo "📁 APK location:"
        echo "   $DOWNLOAD_DIR/$APK_NAME"
        echo ""
        echo "📱 To install:"
        echo "   1. Enable 'Unknown sources' in Android settings"
        echo "   2. Run: termux-open \"$DOWNLOAD_DIR/$APK_NAME\""
        echo "   3. Or open file manager and tap the APK"
        echo ""
        
        # Ask to install now
        read -p "Install now? [Y/n]: " choice
        if [[ ! $choice =~ ^[Nn]$ ]]; then
            echo "🔧 Opening installer..."
            termux-open "$DOWNLOAD_DIR/$APK_NAME"
            echo ""
            echo "✅ Check your phone screen to complete installation!"
            echo ""
            echo "💡 After installation:"
            echo "   - Press Home button"
            echo "   - Select '4GAuteauOS'"
            echo "   - Tap 'Always'"
        fi
    else
        echo "❌ Downloaded file is too small (may be corrupted)"
        rm -f "$DOWNLOAD_DIR/$APK_NAME"
    fi
else
    echo "❌ Download failed"
    echo ""
    echo "💡 Alternative methods:"
    echo "1. Download manually from:"
    echo "   https://github.com/ether4o4/4GAuteauOS"
    echo "2. Build from source:"
    echo "   git clone https://github.com/ether4o4/4GAuteauOS.git"
    echo "   cd 4GAuteauOS"
    echo "   ./gradlew assembleDebug"
fi