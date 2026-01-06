import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatformApplication)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.bundles.serialization)
        }
    }
}


compose.desktop {
    application {
        mainClass = "ke.don.ski_template.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ke.don.ski_template"
            packageVersion = "1.0.0"
        }
    }
}
//tasks.register<JavaExec>("runDesktopPreview") {
//    group = "application"
//    description = "Run the desktop application for preview"
//    mainClass.set("ke.don.ski_template.PreviewMainKt")
//    classpath = sourceSets["jvmMain"].runtimeClasspath
//}