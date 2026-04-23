#!/bin/bash

echo "Updating app branding..."

# Update app name in strings.xml
sed -i 's/Olauncher/4GAuteauOS/g' app/src/main/res/values/strings.xml
sed -i 's/Olauncher Pro/4GAuteauOS Pro/g' app/src/main/res/values/strings.xml

# Update other string files
find app/src/main/res -name "strings.xml" -exec sed -i 's/Olauncher/4GAuteauOS/g' {} \;

# Update launcher icons (we'll create simple replacements)
# For now, just update references
echo "Branding updated to 4GAuteauOS"

# Create a simple README
cat > README.md << 'EOF'
# 4GAuteauOS

A minimalist Android launcher based on Olauncher, customized for VodouLaCroix.

## Features
- Minimalist design
- Fast app launching
- Customizable home screen
- Privacy focused
- No ads, no tracking

## Building
1. Open in Android Studio
2. Build -> Build Bundle(s) / APK(s)
3. Install on your device

## Customization
- Package: com.vodoulacroix.launcher
- App name: 4GAuteauOS
- Branding: Custom for VodouLaCroix

## Credits
Based on Olauncher by tanujnotes (https://github.com/tanujnotes/Olauncher)
EOF

echo "README created"