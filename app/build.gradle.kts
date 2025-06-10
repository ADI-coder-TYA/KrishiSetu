import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.cyberlabs.krishisetu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cyberlabs.krishisetu"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Read API key from local.properties for direct access (DEV ONLY)
        val geminiApiKey = run {
            var key: String? = System.getenv("GEMINI_API_KEY") // Check environment variables first
            if (key == null) {
                val localPropertiesFile = rootProject.file("local.properties")
                if (localPropertiesFile.exists()) {
                    val properties = Properties()
                    localPropertiesFile.inputStream().use { properties.load(it) }
                    key = properties.getProperty("GEMINI_API_KEY")
                }
            }
            key
        }

        if (geminiApiKey == null) {
            // Log a warning if the key isn't found
            println("WARNING: GEMINI_API_KEY not found. Make sure it's in local.properties or environment variables.")
            // You might also want to fail the build in a CI/CD environment here:
            // throw GradleException("GEMINI_API_KEY is not defined!")
        }

        // Make the API key available as a BuildConfig field
        // Ensure the value is enclosed in double quotes for String type
        buildConfigField("String", "GEMINI_API_KEY", "\"${geminiApiKey}\"")
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    //Coil-Compose
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")

    //Navigation-Compose
    implementation("androidx.navigation:navigation-compose:2.9.0")

    //Dagger-Hilt
    implementation("com.google.dagger:hilt-android:2.56.2")
    ksp("com.google.dagger:hilt-android-compiler:2.56.2")
    //Navigation-Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    //Core library desugaring support
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // Kotlin Coroutines (optional but recommended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // AndroidX Lifecycle (for LiveData, ViewModel, etc.)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")

    // Amplify core
    implementation("com.amplifyframework:core:2.27.3")

    implementation("androidx.compose.material3:material3")
    // For Material 3's ModalBottomSheet and other window-size-aware components
    implementation("androidx.compose.material3:material3-window-size-class:1.3.2")

    // Auth with Cognito
    implementation("com.amplifyframework:aws-auth-cognito:2.27.3")

    // GraphQL API
    implementation("com.amplifyframework:aws-api:2.27.3")

    // DataStore (syncs with DynamoDB under the hood)
    implementation("com.amplifyframework:aws-datastore:2.27.3")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0") // Use the latest stable version if available

    // S3 Storage
    implementation("com.amplifyframework:aws-storage-s3:2.27.3")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}