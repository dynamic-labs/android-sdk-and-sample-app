import org.gradle.authentication.http.BasicAuthentication

// Check if JFrog credentials are available (optional - for Fireblocks embedded wallets)
val jfrogUsername: String? = System.getenv("JFROG_USERNAME")
val jfrogToken: String? = System.getenv("JFROG_ACCESS_TOKEN")
val hasJfrogCredentials = !jfrogUsername.isNullOrBlank() && !jfrogToken.isNullOrBlank()

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.7.3"
        id("org.jetbrains.kotlin.android") version "2.1.0"
        id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
        id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // JFrog repository (optional - only for Fireblocks embedded wallets)
        // If you don't have credentials, embedded wallet features won't be available
        // but the SDK will still work with external wallets
        if (hasJfrogCredentials) {
            maven {
                name = "JFrog"
                url = uri("https://fbinfra555artifactory.jfrog.io/artifactory/fireblocks-gradle")
                credentials {
                    username = jfrogUsername
                    password = jfrogToken
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "DynamicSDKExample"

// Log JFrog status
if (hasJfrogCredentials) {
    println("JFrog credentials found - Fireblocks embedded wallet support enabled")
} else {
    println("JFrog credentials not found - Fireblocks embedded wallet support disabled")
    println("Set JFROG_USERNAME and JFROG_ACCESS_TOKEN environment variables to enable")
}
