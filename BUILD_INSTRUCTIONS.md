# VodouLauncher - Build Instructions

## Prerequisites
1. **Android Studio** (latest version)
2. **Java JDK 17** (included with Android Studio)
3. **Android SDK** (included with Android Studio)

## Building the APK

### Method 1: Using Android Studio GUI
1. Open Android Studio
2. Select "Open" and choose the `my-android-launcher` folder
3. Wait for Gradle sync to complete
4. Connect your Android device or start an emulator
5. Click **Run** → **Run 'app'** (or press Shift+F10)

### Method 2: Using Command Line (Windows)
```cmd
# Open Command Prompt in project directory
cd my-android-launcher

# Build debug APK
gradlew assembleDebug

# Build release APK (requires signing)
gradlew assembleRelease
```

The APK will be generated at:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## Installing on Your Device

### Option A: Direct Install via Android Studio
1. Connect your Android device via USB
2. Enable USB debugging in Developer Options
3. Run the app from Android Studio

### Option B: Manual APK Install
1. Transfer the APK to your device
2. Enable "Install from unknown sources" in settings
3. Open the APK file and install

## Custom Features Added

### 1. Custom Branding
- Package name: `com.vodoulacroix.launcher`
- App name: `VodouLauncher`
- Updated all references from Olauncher to VodouLauncher

### 2. Welcome Message System
- Custom welcome toast on first launch
- Time-based greetings (morning/afternoon/evening)
- Version info display

### 3. Project Structure
- Clean package organization
- All files properly renamed
- README and documentation

## Troubleshooting

### Build Errors
1. **Gradle sync failed**: Try `File` → `Invalidate Caches and Restart`
2. **Missing SDK**: Install required SDK versions in SDK Manager
3. **Java version issues**: Ensure JDK 17 is set in Project Structure

### Installation Issues
1. **App not installing**: Check if you have an existing Olauncher installed (uninstall first)
2. **USB debugging not working**: Re-enable Developer Options
3. **Signature conflicts**: Uninstall previous versions first

## Next Steps

### To Further Customize:
1. **Icons**: Replace `app/src/main/res/mipmap-*/ic_launcher.png`
2. **Colors**: Modify `app/src/main/res/values/colors.xml`
3. **Strings**: Update `app/src/main/res/values/strings.xml`
4. **Features**: Add more custom Kotlin files in the helper package

### To Add AI Features (from your other projects):
1. Integrate with your Colour-Ceauxdid platform
2. Add AI-powered app suggestions
3. Implement smart folder organization

## Support
If you encounter any issues:
1. Check Android Studio error messages
2. Verify Gradle build logs
3. Ensure all dependencies are synced

## Ready to Build!
Your customized Android launcher is ready. Open it in Android Studio and build your first APK! 🚀