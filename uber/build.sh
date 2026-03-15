#!/bin/bash
# Render Build Script

echo "🚀 Starting build process..."

# Make mvnw executable
chmod +x mvnw

# Clean and build
echo "📦 Building application..."
./mvnw clean package -DskipTests

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📁 JAR file created in target/"
    ls -lh target/*.jar
else
    echo "❌ Build failed!"
    exit 1
fi
