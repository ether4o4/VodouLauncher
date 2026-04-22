#!/bin/bash

# Script to rename package from app.olauncher to com.vodoulacroix.launcher

OLD_PACKAGE="app.olauncher"
NEW_PACKAGE="com.vodoulacroix.launcher"
OLD_DIR="app/olauncher"
NEW_DIR="com/vodoulacroix/launcher"

echo "Renaming package from $OLD_PACKAGE to $NEW_PACKAGE"

# 1. Update build.gradle
sed -i "s/applicationId \"$OLD_PACKAGE\"/applicationId \"$NEW_PACKAGE\"/g" app/build.gradle
sed -i "s/namespace '$OLD_PACKAGE'/namespace '$NEW_PACKAGE'/g" app/build.gradle

# 2. Update AndroidManifest.xml
sed -i "s/\.MainActivity/.$NEW_PACKAGE.MainActivity/g" app/src/main/AndroidManifest.xml
sed -i "s/\.helper/.$NEW_PACKAGE.helper/g" app/src/main/AndroidManifest.xml
sed -i "s/\.listener/.$NEW_PACKAGE.listener/g" app/src/main/AndroidManifest.xml

# 3. Create new directory structure
mkdir -p "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')"
mkdir -p "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/helper"
mkdir -p "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/helper/usageStats"
mkdir -p "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/ui"
mkdir -p "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/data"
mkdir -p "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/listener"

# 4. Move files and update package declarations
find app/src/main/java -name "*.kt" -o -name "*.java" | while read file; do
    if [ -f "$file" ]; then
        # Update package declaration in file
        sed -i "s/package $OLD_PACKAGE/package $NEW_PACKAGE/g" "$file"
        sed -i "s/package $OLD_PACKAGE.helper/package $NEW_PACKAGE.helper/g" "$file"
        sed -i "s/package $OLD_PACKAGE.ui/package $NEW_PACKAGE.ui/g" "$file"
        sed -i "s/package $OLD_PACKAGE.data/package $NEW_PACKAGE.data/g" "$file"
        sed -i "s/package $OLD_PACKAGE.listener/package $NEW_PACKAGE.listener/g" "$file"
        
        # Update imports
        sed -i "s/import $OLD_PACKAGE/import $NEW_PACKAGE/g" "$file"
    fi
done

# 5. Move files to new location
mv app/src/main/java/app/olauncher/* "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/" 2>/dev/null || true
mv app/src/main/java/app/olauncher/helper/* "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/helper/" 2>/dev/null || true
mv app/src/main/java/app/olauncher/helper/usageStats/* "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/helper/usageStats/" 2>/dev/null || true
mv app/src/main/java/app/olauncher/ui/* "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/ui/" 2>/dev/null || true
mv app/src/main/java/app/olauncher/data/* "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/data/" 2>/dev/null || true
mv app/src/main/java/app/olauncher/listener/* "app/src/main/java/$(echo $NEW_PACKAGE | sed 's/\./\//g')/listener/" 2>/dev/null || true

# 6. Clean up old directories
rm -rf app/src/main/java/app/olauncher

# 7. Update settings.gradle
sed -i "s/:app/:app/g" settings.gradle  # Keep as is

echo "Package rename complete!"