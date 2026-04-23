#!/data/data/com.termux/files/usr/bin/bash

# 4GAuteauOS APK Finder & Installer
# Helps locate and install the downloaded APK

echo "🔍 4GAuteauOS APK Finder"
echo "========================"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check Termux
if [ ! -d "/data/data/com.termux/files/usr" ]; then
    echo -e "${RED}❌ Run this in Termux${NC}"
    exit 1
fi

echo -e "${YELLOW}🔍 Searching for 4GAuteauOS APK...${NC}"

# Search in common locations
APK_FOUND=""
SEARCH_PATHS=(
    "$HOME/4gauteauos.apk"
    "$HOME/4gauteauos-debug.apk"
    "$HOME/4gauteauos*.apk"
    "$PWD/4gauteauos.apk"
    "$PWD/4gauteauos-debug.apk"
    "/sdcard/Download/4gauteauos.apk"
    "/sdcard/Download/4gauteauos-debug.apk"
    "$HOME/storage/downloads/4gauteauos.apk"
    "$HOME/storage/downloads/4gauteauos-debug.apk"
)

for path in "${SEARCH_PATHS[@]}"; do
    if [ -f "$path" ]; then
        APK_FOUND="$path"
        echo -e "${GREEN}✅ Found APK: $path${NC}"
        ls -lh "$path"
        break
    fi
done

# If not found, search recursively
if [ -z "$APK_FOUND" ]; then
    echo -e "${YELLOW}🔍 Searching recursively...${NC}"
    APK_FOUND=$(find /sdcard /storage /data/data/com.termux/files/home -name "*4gauteauos*.apk" -type f 2>/dev/null | head -1)
    
    if [ -n "$APK_FOUND" ]; then
        echo -e "${GREEN}✅ Found APK: $APK_FOUND${NC}"
        ls -lh "$APK_FOUND"
    fi
fi

if [ -z "$APK_FOUND" ]; then
    echo -e "${RED}❌ No APK found${NC}"
    echo ""
    echo -e "${YELLOW}📥 Let's download it again:${NC}"
    echo "Run these commands:"
    echo ""
    echo "1. Download:"
    echo "   curl -L -o ~/4gauteauos.apk \\"
    echo "   https://github.com/ether4o4/4GAuteauOS/releases/latest/download/4gauteauos-debug.apk"
    echo ""
    echo "2. Install:"
    echo "   termux-open ~/4gauteauos.apk"
    echo ""
    echo "3. Or use this one-liner:"
    echo "   curl -L -o ~/4gauteauos.apk https://github.com/ether4o4/4GAuteauOS/releases/latest/download/4gauteauos-debug.apk && termux-open ~/4gauteauos.apk"
    exit 1
fi

# Ask to install
echo ""
echo -e "${GREEN}📱 APK found!${NC}"
echo "Size: $(stat -c%s "$APK_FOUND" | awk '{print int($1/1024/1024)"MB"}')"
echo ""
read -p "Install 4GAuteauOS now? [Y/n]: " choice

if [[ $choice =~ ^[Nn]$ ]]; then
    echo -e "${YELLOW}APK location: $APK_FOUND${NC}"
    echo "Install manually with: termux-open \"$APK_FOUND\""
    exit 0
fi

# Grant storage permission if needed
echo -e "${YELLOW}🔧 Setting up permissions...${NC}"
termux-setup-storage

# Install
echo -e "${YELLOW}🚀 Installing 4GAuteauOS...${NC}"
echo ""
echo -e "${GREEN}📋 IMPORTANT:${NC}"
echo "1. Your phone will show the installer"
echo "2. Tap 'Install' when prompted"
echo "3. Enable 'Unknown sources' if needed"
echo "4. After install, press Home button"
echo "5. Select '4GAuteauOS' → Tap 'Always'"
echo ""
read -p "Press Enter to continue..."

termux-open "$APK_FOUND"

echo ""
echo -e "${GREEN}✅ Installation started!${NC}"
echo "Check your phone screen to complete."
echo ""
echo -e "${YELLOW}💡 If installation doesn't start:${NC}"
echo "1. Open your file manager app"
echo "2. Navigate to: $(dirname "$APK_FOUND")"
echo "3. Tap on: $(basename "$APK_FOUND")"
echo "4. Follow on-screen instructions"