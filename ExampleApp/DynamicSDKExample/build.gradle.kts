plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// AAR file paths (relative to this build.gradle.kts)
val sdkAarPath = "../../dynamic-sdk-android.aar"
val solanaAarPath = "../../solana-web3.aar"

android {
    namespace = "com.dynamic.sdk.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dynamic.sdk.example"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ==========================================
    // Dynamic SDK AAR Files
    // ==========================================

    // Core SDK (required)
    implementation(files(sdkAarPath))

    // SolanaWeb3 SDK (for Solana transactions)
    implementation(files(solanaAarPath))
    implementation("org.sol4k:sol4k:0.6.0")

    // ==========================================
    // Required transitive dependencies for SDK
    // ==========================================

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // JSON serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Android WebView
    implementation("androidx.webkit:webkit:1.8.0")

    // HTTP client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Custom Tabs for authentication
    implementation("androidx.browser:browser:1.7.0")

    // Secure storage - DataStore + Tink (modern, future-proof)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.crypto.tink:tink-android:1.15.0")

    // Passkeys support
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Jetpack Compose (required for DynamicUI)
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // ==========================================
    // App-specific dependencies
    // ==========================================

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // QR Code generation
    implementation("com.google.zxing:core:3.5.2")

    // ==========================================
    // Testing
    // ==========================================

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
