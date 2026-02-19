# TALKBIM Avatar Integration - Setup Instructions

## 🎯 Overview

This guide walks you through setting up the avatar integration system step-by-step.

## 📋 Prerequisites

- Android Studio 4.0+
- Kotlin 1.5+
- Gradle 8.0+
- Streamoji API Key (sign up at https://avatars.streamoji.com)

## 🚀 Setup Steps

### Step 1: Clone Repository
\`\`\`bash
git clone https://github.com/HansAndre22/TALKBIM-Avatar-Integration.git
cd TALKBIM-Avatar-Integration
\`\`\`

### Step 2: Prepare Your TALKBIM Project
1. Open your existing TALKBIM project
2. Note your package name (e.g., com.example.myapplication)

### Step 3: Copy Source Files
- Copy `src/main/java/com/example/myapplication/*` to your project
- Update package names to match your project
- Copy XML layout files to `app/src/main/res/layout/`

### Step 4: Add Dependencies

Add to `app/build.gradle`:

\`\`\`gradle
dependencies {
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'

    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'

    // Lifecycle
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.5.1'

    // Hilt
    implementation 'com.google.dagger:hilt-android:2.44'
    kapt 'com.google.dagger:hilt-compiler:2.44'

    // 3D Rendering
    implementation 'com.google.ar.sceneform:sceneform:1.21.0'

    // GLTF
    implementation 'io.github.sceneview:arsceneview:2.0.0'

    // JSON
    implementation 'com.google.code.gson:gson:2.9.0'
}
\`\`\`

### Step 5: Configure Streamoji API

1. Sign up: https://avatars.streamoji.com
2. Get API key
3. Add to `local.properties`:
   \`\`\`
   streamoji.api.key=YOUR_API_KEY_HERE
   \`\`\`

4. In `build.gradle`:
   \`\`\`gradle
   buildTypes {
       debug {
           buildConfigField "String", "STREAMOJI_API_KEY", 
               "\"\${project.properties['streamoji.api.key'] ?: ''}\""
       }
   }
   \`\`\`

### Step 6: Update AndroidManifest.xml

Add permissions:
\`\`\`xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
\`\`\`

### Step 7: Initialize Hilt

Create Application class:
\`\`\`kotlin
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
\`\`\`

Update AndroidManifest.xml:
\`\`\`xml
<application android:name=".MyApplication" ...>
</application>
\`\`\`

### Step 8: Test Setup

Build the project: `./gradlew build`

## ✅ Verification

- [ ] All dependencies added
- [ ] Package names updated
- [ ] API key configured
- [ ] Project builds without errors
- [ ] No compile errors

## 🎯 Next Steps

1. Follow `INTEGRATION_GUIDE.md`
2. Connect MediaPipe detection
3. Map signs to animations
4. Test avatar display

## 🆘 Troubleshooting

- Build fails? Check gradle sync and dependencies
- API key not found? Verify local.properties
- Package name issues? Search-replace all files

See `docs/TROUBLESHOOTING.md` for more help.
