import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

// API hosts come from local.properties (untracked) so hosts/IPs are not baked
// into source control. Mirrors the web app's VITE_API_URL family. Defaults point
// at the public dev hosts.
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun prop(key: String, default: String): String =
    (localProps.getProperty(key) ?: System.getenv(key) ?: default)

android {
    namespace = "com.amoozim.creator.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_URL", "\"${prop("API_URL", "https://api-dev.amoozim.com/api")}\"")
        buildConfigField("String", "API_URL_ANALYTICS", "\"${prop("API_URL_ANALYTICS", "https://analytics-dev.amoozim.com/api")}\"")
        buildConfigField("String", "STORAGE_API_BASE", "\"${prop("STORAGE_API_BASE", "https://storage.amoozim.com/api")}\"")
        buildConfigField("String", "STORAGE_PREVIEW_BASE", "\"${prop("STORAGE_PREVIEW_BASE", "https://storage.amoozim.com")}\"")
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets["main"].kotlin.srcDirs("src/main/kotlin")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":core:common"))
    api(project(":core:model"))

    api(libs.bundles.networking)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
