# 📱 4GAuteauOS - Termux Installation Guide

## **Option 1: Download Pre-built APK (Easiest)**

### **Direct Download (Run in Termux):**
```bash
# Download the APK directly
pkg install wget -y
wget https://github.com/ether4o4/4GAuteauOS/releases/latest/download/4gauteauos-debug.apk

# Install it
termux-open 4gauteauos-debug.apk
```

### **Or use this one-liner:**
```bash
curl -L -o 4gauteauos.apk https://github.com/ether4o4/4GAuteauOS/releases/latest/download/4gauteauos-debug.apk && termux-open 4gauteauos.apk
```

## **Option 2: Build on Computer & Transfer**

### **Step 1: Build on Computer**
On your computer (Windows/Mac/Linux):

```bash
git clone https://github.com/ether4o4/4GAuteauOS.git
cd 4GAuteauOS
./build_local.sh  # or use Android Studio
```

### **Step 2: Transfer to Phone**
1. Connect phone to computer via USB
2. Enable file transfer mode
3. Copy `4gauteauos-debug.apk` to phone's Downloads folder
4. Install from phone's file manager

### **Step 3: Install via Termux**
```bash
# If APK is in Downloads
termux-open /sdcard/Download/4gauteauos-debug.apk

# Or if using Termux storage access
termux-setup-storage
cp ~/storage/downloads/4gauteauos-debug.apk ~/
termux-open ~/4gauteauos-debug.apk
```

## **Option 3: Build Directly on Termux (Advanced)**

**Note:** This requires 2GB+ free space and may fail due to Android SDK requirements.

```bash
# Install basic tools
pkg update && pkg upgrade
pkg install openjdk-17 git wget unzip

# Clone repository
git clone https://github.com/ether4o4/4GAuteauOS.git
cd 4GAuteauOS

# Try to build (may fail without Android SDK)
chmod +x gradlew
./gradlew assembleDebug

# If successful, install
find . -name "*.apk" -type f | head -1 | xargs termux-open
```

## **📱 Installation Steps:**

1. **Enable Unknown Sources:**
   - Go to Settings → Security → Unknown Sources
   - Enable installation from unknown sources

2. **Install APK:**
   - Open file manager
   - Find `4gauteauos-debug.apk`
   - Tap to install

3. **Set as Default Launcher:**
   - After installation, press Home button
   - Select "4GAuteauOS"
   - Tap "Always"

4. **Enjoy 4GAuteauOS!**
   - Vista-evolved glass UI
   - Futuristic design
   - Performance optimized

## **🔧 Troubleshooting:**

### **If APK won't install:**
```bash
# Check Android version (needs 8.0+)
termux-info | grep "Android version"

# Clear package manager cache
pm clear com.android.packageinstaller
```

### **If no file manager:**
```bash
# Install a simple file manager
pkg install termux-api -y
termux-open .
```

### **If storage access denied:**
```bash
# Grant storage permission
termux-setup-storage

# List downloads
ls ~/storage/downloads/
```

## **💡 Quick Commands:**

```bash
# Download latest APK
curl -L -o 4gauteauos.apk https://github.com/ether4o4/4GAuteauOS/releases/latest/download/4gauteauos-debug.apk

# Install from Termux
termux-open 4gauteauos.apk

# Check if installed
pm list packages | grep fourgauteau

# Launch 4GAuteauOS
am start -n com.fourgauteau.os/.MainActivity
```

## **📞 Support:**

- **GitHub Issues:** https://github.com/ether4o4/4GAuteauOS/issues
- **Repository:** https://github.com/ether4o4/4GAuteauOS
- **Build Instructions:** See `BUILD_INSTRUCTIONS.md`

## **🎉 Welcome to 4GAuteauOS!**

Your Vista-evolved Android launcher with futuristic glass design, built for music and AI experiences. Perfect for daily use with premium feel and smooth performance.