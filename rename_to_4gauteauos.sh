#!/bin/bash

echo "🚀 Renaming project to 4GAuteauOS..."
echo "==================================="

# Update all text files
echo "Updating text files..."

# Update 4GAuteauOS to 4GAuteauOS (case sensitive)
find . -type f \( -name "*.kt" -o -name "*.java" -o -name "*.xml" -o -name "*.gradle" -o -name "*.md" -o -name "*.sh" -o -name "*.yml" -o -name "*.toml" \) \
  -exec sed -i 's/4GAuteauOS/4GAuteauOS/g' {} \;

# Update 4gauteauos to 4gauteauos (lowercase)
find . -type f \( -name "*.kt" -o -name "*.java" -o -name "*.xml" -o -name "*.gradle" -o -name "*.md" -o -name "*.sh" -o -name "*.yml" -o -name "*.toml" \) \
  -exec sed -i 's/4gauteauos/4gauteauos/g' {} \;

# Update 4GAUTEAU OS to 4GAUTEAU OS (uppercase)
find . -type f \( -name "*.kt" -o -name "*.java" -o -name "*.xml" -o -name "*.gradle" -o -name "*.md" -o -name "*.sh" -o -name "*.yml" -o -name "*.toml" \) \
  -exec sed -i 's/4GAUTEAU OS/4GAUTEAU OS/g' {} \;

echo "✅ Text files updated"

# Update directory names if needed
echo "Checking directory structure..."

# Update settings.gradle
if [ -f "settings.gradle" ]; then
  sed -i 's/rootProject.name = "4GAuteauOS"/rootProject.name = "4GAuteauOS"/g' settings.gradle
  echo "✅ Updated settings.gradle"
fi

# Update README.md title
if [ -f "README.md" ]; then
  sed -i '1s/.*/# 4GAuteauOS 🎵🤖/' README.md
  echo "✅ Updated README.md"
fi

echo ""
echo "🎉 Project renamed to 4GAuteauOS!"
echo ""
echo "Summary of changes:"
echo "- Package: com.fourgauteau.os"
echo "- App name: 4GAuteauOS"
echo "- All references updated"
echo "- Ready to build as 4GAuteauOS"
echo ""
echo "Next steps:"
echo "1. Run: ./gradlew clean"
echo "2. Run: ./gradlew assembleDebug"
echo "3. Install 4GAuteauOS APK on device"