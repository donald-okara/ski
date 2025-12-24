val appName: String by settings

rootProject.name = appName
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// App
include(":composeApp")
includeBuild("build-logic")

// Folders
include(":core")
include(":segments")
include(":datasource")
include(":shared")

// Core
include(":core:domain")
include(":core:utils")
include(":core:navigation")

// Datasource
include(":datasource:remote")
include(":datasource:local")

// Segments
include(":segments:introduction")
include(":segments:demos")

// Shared UI
include(":shared:design")
include(":shared:resources")
include(":shared:components")

