pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "AmoozimCreator"

include(":app")

// Core: shared, cross-cutting infrastructure (no feature logic).
include(":core:common")        // Result types, dispatchers, pure utils
include(":core:model")         // shared serializable models (envelope, Profile, auth DTOs)
include(":core:network")       // Retrofit/OkHttp, interceptors, auth contracts, safeApiCall
include(":core:session")       // token store + SessionManager (implements network auth contracts)
include(":core:designsystem")  // theme + generic Compose components

// Features: self-contained vertical slices (model + data + ui), depend only on :core:*.
include(":feature:entry")      // session bootstrap (initData -> JWT)
include(":feature:miniapp")    // mini-app details + header
include(":feature:course")     // courses list + course detail (lessons)
include(":feature:profile")    // current-user profile
