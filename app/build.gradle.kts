plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.amoozim.creator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.amoozim.creator"
        minSdk = 26 // adaptive (vector) launcher icons; covers Android 8.0+
        targetSdk = 35
        versionCode = 1
        versionName = "1.1.9" // tracks the web app version line

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Persian (fa) is the primary locale; ship it explicitly.
        resourceConfigurations += listOf("fa", "en")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { compose = true }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    sourceSets["main"].kotlin.srcDirs("src/main/kotlin")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Core
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:session"))
    implementation(project(":core:designsystem"))

    // Features
    implementation(project(":feature:entry"))
    implementation(project(":feature:miniapp"))
    implementation(project(":feature:course"))
    implementation(project(":feature:profile"))

    // Android / Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.lifecycle.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Networking (OkHttp shared with Coil image loader)
    implementation(libs.okhttp)

    // Images
    implementation(libs.coil.compose)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
