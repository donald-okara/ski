package ke.don.ski.extensions

import appIdentity
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureComponents(
    extension: KotlinMultiplatformExtension
) = extension.apply {
    val useLocal = appIdentity.localComponents

    sourceSets.apply {
        commonMain {
            if (useLocal){
                dependencies {
                    implementation(project(":shared:components"))
                }
            } else {
                dependencies {
                    implementation(libs.findLibrary("ski-components").get())
                }
            }
        }
    }
}