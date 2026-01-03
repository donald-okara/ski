plugins {
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatformPlugin)
}

kotlin {
    sourceSets{
        commonMain.dependencies {
            implementation(project(":shared:components"))
            implementation(project(":core:domain"))
        }
    }
}