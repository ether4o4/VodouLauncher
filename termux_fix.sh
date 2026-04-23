#!/data/data/com.termux/files/usr/bin/bash

# 4GAuteauOS Termux Fix Script
# Helps install 4GAuteauOS when downloads fail

echo "🔧 4GAuteauOS Installation Fix"
echo "=============================="

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check storage access
echo -e "${YELLOW}📁 Checking storage access...${NC}"
termux-setup-storage

# Method 1: Try to find existing 4GAuteauOS APK
echo -e "${YELLOW}🔍 Searching for existing APKs...${NC}"
FOUND_APK=""
for apk in ~/storage/downloads/*.apk /sdcard/Download/*.apk ~/*.apk; do
    if [ -f "$apk" ]; then
        # Check if it might be 4GAuteauOS by size (should be > 1MB)
        SIZE=$(stat -c%s "$apk" 2>/dev/null || echo 0)
        if [ $SIZE -gt 1000000 ]; then
            echo -e "${GREEN}📦 Found APK: $(basename "$apk") (${SIZE} bytes)${NC}"
            FOUND_APK="$apk"
            break
        fi
    fi
done

if [ -n "$FOUND_APK" ]; then
    echo -e "${GREEN}✅ Found suitable APK${NC}"
    echo "File: $FOUND_APK"
    echo "Size: $(($SIZE/1024/1024))MB"
    echo ""
    read -p "Try to install this APK? [Y/n]: " choice
    if [[ ! $choice =~ ^[Nn]$ ]]; then
        echo -e "${YELLOW}🚀 Installing...${NC}"
        termux-open "$FOUND_APK"
        echo -e "${GREEN}✅ Check your phone screen to complete installation${NC}"
        exit 0
    fi
fi

# Method 2: Try to download again with different methods
echo -e "${YELLOW}📥 Trying to download 4GAuteauOS...${NC}"

# Try multiple URLs
URLS=(
    "https://github.com/ether4o4/4GAuteauOS/releases/latest/download/4gauteauos-debug.apk"
    "https://raw.githubusercontent.com/ether4o4/4GAuteauOS/main/app/build/outputs/apk/debug/app-debug.apk"
    "https://transfer.sh/4gauteauos-debug.apk"
)

OUTPUT="/sdcard/Download/4gauteauos_fixed.apk"
SUCCESS=0

for url in "${URLS[@]}"; do
    echo "Trying: $(echo "$url" | cut -d'/' -f3)"
    
    # Try curl
    curl -L -o "$OUTPUT" "$url" 2>/dev/null
    
    # Check if download succeeded
    if [ -f "$OUTPUT" ] && [ $(stat -c%s "$OUTPUT" 2>/dev/null || echo 0) -gt 1000000 ]; then
        echo -e "${GREEN}✅ Download successful!${NC}"
        SUCCESS=1
        break
    else
        rm -f "$OUTPUT" 2>/dev/null
        # Try wget
        wget -O "$OUTPUT" "$url" 2>/dev/null
        if [ -f "$OUTPUT" ] && [ $(stat -c%s "$OUTPUT" 2>/dev/null || echo 0) -gt 1000000 ]; then
            echo -e "${GREEN}✅ Download successful!${NC}"
            SUCCESS=1
            break
        fi
        rm -f "$OUTPUT" 2>/dev/null
    fi
done

if [ $SUCCESS -eq 1 ]; then
    echo -e "${GREEN}📦 APK ready: $OUTPUT${NC}"
    echo "Size: $(ls -lh "$OUTPUT" | awk '{print $5}')"
    echo ""
    echo -e "${YELLOW}📱 To install:${NC}"
    echo "1. Enable 'Unknown sources' in Android settings"
    echo "2. Run: termux-open \"$OUTPUT\""
    echo "3. Or open file manager and tap the APK"
    echo ""
    read -p "Install now? [Y/n]: " choice
    if [[ ! $choice =~ ^[Nn]$ ]]; then
        termux-open "$OUTPUT"
    fi
else
    echo -e "${RED}❌ All download methods failed${NC}"
    echo ""
    echo -e "${YELLOW}💡 Alternative solutions:${NC}"
    echo "1. Build from source:"
    echo "   git clone https://github.com/ether4o4/4GAuteauOS.git"
    echo "   cd 4GAuteauOS"
    echo "   ./gradlew assembleDebug"
    echo ""
    echo "2. Download on computer and transfer via USB"
    echo ""
    echo "3. Use a different launcher temporarily"
    echo "   termux-open ~/storage/downloads/computer-launcher.apk"
fi

echo ""
echo -e "${GREEN}✨ 4GAuteauOS Features:${NC}"
echo "- Vista-evolved glass UI"
echo "- Futuristic design"
echo "- Performance optimized"
echo "- Music & AI focused"