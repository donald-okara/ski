
plugins {
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.composeMultiplatformPlugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.serialization)
            implementation(libs.kotlinx.coroutines.core)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
        }
    }
}
