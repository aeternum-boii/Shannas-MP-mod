#!/bin/bash
set -euo pipefail

MOD_DIR="/srv/modding/shannaMod"
VERSION_FILE="$MOD_DIR/alphaRunningVersion"
DESCRIPTOR="$MOD_DIR/mod/descriptor.mod"
BUILD_CONFIG="$MOD_DIR/alphaBuildConfig.vdf"

cd "$MOD_DIR"

echo "Pulling latest changes..."
git pull --ff-only

# Read and validate the current alpha version.
VERSION=$(<"$VERSION_FILE")

if [[ ! "$VERSION" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)-alpha\.([0-9]+)$ ]]; then
    echo "Invalid alpha version: $VERSION" >&2
fi

MAJOR="${BASH_REMATCH[1]}"
MINOR="${BASH_REMATCH[2]}"
PATCH="${BASH_REMATCH[3]}"
ALPHA="${BASH_REMATCH[4]}"

# Increment alpha number.
((ALPHA++))

NEW_VERSION="${MAJOR}.${MINOR}.${PATCH}-alpha.${ALPHA}"

echo "Version: $VERSION -> $NEW_VERSION"

# Update alphaRunningVersion.
printf '%s\n' "$NEW_VERSION" > "$VERSION_FILE"

# Update version= in descriptor.mod.
sed -i -E \
    "s/^version=\"[^\"]*\"$/version=\"$NEW_VERSION\"/" \
    "$DESCRIPTOR"

# Make sure the descriptor actually contained a version line.
if ! grep -q "^version=\"$NEW_VERSION\"$" "$DESCRIPTOR"; then
    echo "Failed to update version in $DESCRIPTOR" >&2
    exit 1
fi

echo "Committing version $NEW_VERSION..."
git add "$VERSION_FILE" "$DESCRIPTOR"
git commit -m "Bump alpha version to $NEW_VERSION"

echo "Pushing version $NEW_VERSION..."
git push

echo "Building and uploading workshop item..."
steamcmd \
    +login simanthegrate \
    +workshop_build_item "$BUILD_CONFIG" \
    +quit

echo "Successfully uploaded $NEW_VERSION"
