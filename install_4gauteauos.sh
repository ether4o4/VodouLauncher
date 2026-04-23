#!/data/data/com.termux/files/usr/bin/bash

# 4GAuteauOS One-Click Termux Installer
# Run this in Termux to download and install 4GAuteauOS

echo "🚀 4GAuteauOS Termux Installer"
echo "=============================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if running in Termux
if [ ! -d "/data/data/com.termux/files/usr" ]; then
    echo -e "${RED}❌ This script must be run in Termux on Android${NC}"
    echo "   Install Termux from F-Droid: https://f-droid.org/packages/com.termux/"
    exit 1
fi

# Update packages
echo -e "${YELLOW}📦 Updating Termux packages...${NC}"
pkg update -y && pkg upgrade -y

# Install required tools
echo -e "${YELLOW}📦 Installing required tools...${NC}"
pkg install -y curl termux-api wget

# Ask user for installation method
echo ""
echo -e "${GREEN}Choose installation method:${NC}"
echo "1) Download pre-built APK (Recommended)"
echo "2) Build from source (Advanced, requires 2GB+ space)"
echo "3) Help with manual installation"
echo ""
read -p "Enter choice [1-3]: " choice

case $choice in
    1)
        # Download pre-built APK
        echo -e "${YELLOW}📥 Downloading 4GAuteauOS APK...${NC}"
        
        # Try multiple download sources
        if curl -L -o 4gauteauos.apk "https://github.com/ether4o4/4GAuteauOS/releases/latest/download/4gauteauos-debug.apk"; then
            echo -e "${GREEN}✅ APK downloaded successfully${NC}"
        else
            echo -e "${YELLOW}⚠️  GitHub download failed, trying alternative...${NC}"
            # Alternative download
            wget -O 4gauteauos.apk "https://transfer.sh/4gauteauos-debug.apk" || {
                echo -e "${RED}❌ Download failed${NC}"
                echo "Please download manually from:"
                echo "https://github.com/ether4o4/4GAuteauOS"
                exit 1
            }
        fi
        
        # Check file
        if [ -f "4gauteauos.apk" ]; then
            APK_SIZE=$(stat -c%s "4gauteauos.apk")
            if [ $APK_SIZE -lt 10000 ]; then
                echo -e "${RED}❌ Downloaded file is too small (may be corrupted)${NC}"
                rm -f 4gauteauos.apk
                exit 1
            fi
            
            echo -e "${GREEN}📦 APK size: $(($APK_SIZE/1024/1024))MB${NC}"
            
            # Copy to Downloads for easy access
            echo -e "${YELLOW}📁 Copying to Downloads folder...${NC}"
            cp 4gauteauos.apk /sdcard/Download/ 2>/dev/null || \
            cp 4gauteauos.apk ~/storage/downloads/ 2>/dev/null || \
            echo "APK saved to: $(pwd)/4gauteauos.apk"
            
            # Install
            echo -e "${YELLOW}🔧 Installing 4GAuteauOS...${NC}"
            echo ""
            echo -e "${GREEN}📱 IMPORTANT:${NC}"
            echo "1. Enable 'Install from unknown sources' in Android settings"
            echo "2. The installer will open automatically"
            echo "3. Tap 'Install' when prompted"
            echo "4. After installation, press Home button"
            echo "5. Select '4GAuteauOS' and tap 'Always'"
            echo ""
            read -p "Press Enter to continue..."
            
            termux-open 4gauteauos.apk
            
            echo ""
            echo -e "${GREEN}🎉 4GAuteauOS installation started!${NC}"
            echo "Check your phone screen to complete installation."
            
        else
            echo -e "${RED}❌ APK file not found${NC}"
        fi
        ;;
    
    2)
        # Build from source
        echo -e "${YELLOW}🔨 Building from source...${NC}"
        echo "This requires 2GB+ free space and may take 10-20 minutes."
        echo ""
        read -p "Continue? [y/N]: " confirm
        
        if [[ $confirm =~ ^[Yy]$ ]]; then
            # Install build tools
            pkg install -y openjdk-17 git
            
            # Clone and build
            git clone https://github.com/ether4o4/4GAuteauOS.git
            cd 4GAuteauOS
            chmod +x gradlew
            
            echo -e "${YELLOW}Building... This will take a while...${NC}"
            ./gradlew assembleDebug
            
            if [ $? -eq 0 ]; then
                APK_FILE=$(find . -name "*.apk" -type f | head -1)
                if [ -f "$APK_FILE" ]; then
                    echo -e "${GREEN}✅ Build successful!${NC}"
                    termux-open "$APK_FILE"
                fi
            else
                echo -e "${RED}❌ Build failed${NC}"
            fi
        fi
        ;;
    
    3)
        # Help
        echo -e "${GREEN}📖 Manual Installation Guide:${NC}"
        echo ""
        echo "1. Download the APK:"
        echo "   https://github.com/ether4o4/4GAuteauOS"
        echo ""
        echo "2. Transfer to phone:"
        echo "   - USB cable"
        echo "   - Email to yourself"
        echo "   - Cloud storage (Google Drive, Dropbox)"
        echo ""
        echo "3. Install on phone:"
        echo "   - Open file manager"
        echo "   - Find the APK"
        echo "   - Tap to install"
        echo ""
        echo "4. Set as default launcher:"
        echo "   - After install, press Home button"
        echo "   - Select '4GAuteauOS'"
        echo "   - Tap 'Always'"
        ;;
    
    *)
        echo -e "${RED}❌ Invalid choice${NC}"
        ;;
esac

echo ""
echo -e "${GREEN}✨ 4GAuteauOS Features:${NC}"
echo "- Vista-evolved glass UI"
echo "- Futuristic modern design"
echo "- Performance optimized"
echo "- Music & AI focused"
echo ""
echo "Repository: https://github.com/ether4o4/4GAuteauOS"
echo "Enjoy your new launcher! 🎵🤖"