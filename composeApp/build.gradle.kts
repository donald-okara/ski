import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatformApplication)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.bundles.preview)
        }
        commonMain.dependencies {
            implementation(libs.bundles.serialization)
            implementation(compose.components.uiToolingPreview)
        }
    }
}


compose.desktop {
    application {
        mainClass = "ke.don.ski.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ke.don.ski"
            packageVersion = "1.0.0"
        }
    }
}

tasks.register<JavaExec>("runGallery") {
    group = "application"
    description = "Run the component gallery"

    mainClass.set("ke.don.gallery.GalleryMainKt")
    val jvmTarget = kotlin.targets.getByName("jvm") as KotlinJvmTarget
    classpath(jvmTarget.compilations.getByName("main").runtimeDependencyFiles)
}
