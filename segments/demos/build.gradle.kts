
plugins {
    alias(libs.plugins.segmentConvention)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.composemediaplayer)
        }
    }
}