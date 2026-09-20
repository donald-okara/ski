plugins {
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatformPlugin)
}

android {
    namespace = "ke.don.ski.feature"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":segments:introduction"))
            implementation(project(":segments:demos"))
            implementation(project(":shared:design"))
            implementation(project(":shared:resources"))
            implementation(project(":shared:components"))

            implementation(libs.bundles.serialization)
        }
    }
}
