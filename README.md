# Dynamic Android SDK With Sample App

The Dynamic Android SDK provides Web3 authentication and wallet management for Android applications.

## Package Contents

```
Dynamic Android SDK With Sample App/
├── dynamic-sdk-android.aar      # Core SDK (required)
├── solana-web3.aar              # Solana SDK (for SOL/SPL transfers)
├── README.md                    # This file
└── ExampleApp/
    └── DynamicSDKExample/       # Complete sample application
```

## Requirements

- Android SDK 28+ (Android 9.0 Pie)
- Kotlin 2.1.0+
- Gradle 8.x+
- Java 17

## Quick Start with Sample App

1. Open the `ExampleApp/DynamicSDKExample` folder in Android Studio
2. Update the `environmentId` in `MainActivity.kt` with your Dynamic environment ID
3. Build and run the app

## Installation in Your Own Project

### Step 1: Copy AAR Files

Copy the AAR files to your project's `libs/` folder:
- `dynamic-sdk-android.aar` (required)
- `solana-web3.aar` (only if you need Solana support)

### Step 2: Configure build.gradle.kts

```kotlin
dependencies {
    // ==========================================
    // Dynamic SDK AAR Files
    // ==========================================

    // Core SDK (required)
    implementation(files("libs/dynamic-sdk-android.aar"))

    // Solana SDK (optional - remove if not needed)
    implementation(files("libs/solana-web3.aar"))
    implementation("org.sol4k:sol4k:0.6.0")  // Required by Dynamic Solana SDK

    // ==========================================
    // Required transitive dependencies
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

    // Secure storage - DataStore + Tink
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.crypto.tink:tink-android:1.15.0")

    // Passkeys support
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Jetpack Compose (required for DynamicUI)
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
}
```

### Step 3: Configure AndroidManifest.xml

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:usesCleartextTraffic="true"
        ...>

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTask">
            ...
        </activity>

        <!-- OAuth Callback Activity -->
        <activity
            android:name="com.dynamic.sdk.android.Auth.AuthCallbackActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:noHistory="true"
            android:theme="@android:style/Theme.Translucent.NoTitleBar">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="yourappscheme" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

## SDK Usage

### Initialize the SDK

```kotlin
import com.dynamic.sdk.android.DynamicSDK
import com.dynamic.sdk.android.UI.DynamicUI
import com.dynamic.sdk.android.core.ClientProps
import com.dynamic.sdk.android.core.LoggerLevel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val props = ClientProps(
            environmentId = "YOUR_ENVIRONMENT_ID",
            appLogoUrl = "https://your-app.com/logo.png",
            appName = "Your App Name",
            redirectUrl = "yourappscheme://",
            appOrigin = "https://your-app.com",
            logLevel = LoggerLevel.DEBUG
        )
        DynamicSDK.initialize(props, applicationContext, this)

        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                AppContent()
                DynamicUI()  // Required overlay for auth flows
            }
        }
    }
}
```

### Authentication

```kotlin
val sdk = DynamicSDK.getInstance()

// Show auth modal
sdk.ui.showAuth()

// Check auth state
val isAuthenticated = sdk.auth.isAuthenticated()

// Listen to auth changes
sdk.auth.isAuthenticatedFlow.collect { isAuth -> /* ... */ }

// Logout
sdk.auth.logout()
```

### Wallets

```kotlin
val sdk = DynamicSDK.getInstance()

// Get all wallets
val wallets = sdk.wallets.userWallets

// Get primary wallet
val primary = sdk.wallets.primaryWallet
```

### Sign Messages (EVM)

```kotlin
val signature = sdk.evm.signMessage("Hello, Web3!", wallet)
```

### Send Transactions (EVM)

```kotlin
val txHash = sdk.evm.sendTransaction(
    to = "0xRecipientAddress",
    value = "0x0",
    data = "0x",
    wallet = wallet
)
```

### Solana Operations

```kotlin
import com.solanaweb3.*
import org.sol4k.PublicKey

val connection = Connection(Cluster.DEVNET)
val blockhash = connection.getLatestBlockhash()

val instruction = SystemProgram.transfer(
    fromPubkey = PublicKey(wallet.address),
    toPubkey = PublicKey("RecipientAddress"),
    lamports = 1_000_000L
)

val transaction = Transaction.v0(
    payer = PublicKey(wallet.address),
    instructions = listOf(instruction),
    recentBlockhash = blockhash.blockhash
)

val signature = sdk.solana.signAndSendTransaction(
    transaction.serializeUnsignedToBase64(),
    wallet
)
```

## Without Solana Support

If you don't need Solana functionality:

1. Don't copy `solana-web3.aar` to your project
2. Remove the Solana AAR and sol4k dependencies from build.gradle.kts
3. Don't use any Solana-related SDK methods

## Sample App Features

The ExampleApp demonstrates all SDK capabilities:

- Email/SMS authentication
- Social login (Google, Apple, etc.)
- Wallet management
- Message signing
- EVM transactions
- Solana SOL/SPL transfers
- MFA setup
- Passkey authentication

## Troubleshooting

### OAuth Redirect Not Working

Ensure `redirectUrl` in `ClientProps` matches the scheme in AndroidManifest.xml.

### AAR Not Found

Check the file path in `build.gradle.kts` matches the AAR location.

## Support

- [Dynamic Documentation](https://docs.dynamic.xyz)
- Email: support@dynamic.xyz
