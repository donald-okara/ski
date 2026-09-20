import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm {
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":composeApp"))
                implementation(project(":core:domain"))
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
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
    val mainCompilation = jvmTarget.compilations.getByName("main")
    classpath(mainCompilation.output.allOutputs, mainCompilation.runtimeDependencyFiles)
}
