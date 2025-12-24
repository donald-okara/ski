package com.example.app

import com.example.app.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager){
            listOf(
                "composeMultiplatform",
                "composeCompiler",
                "composeHotReload",
            ).forEach { id ->
                pluginManager.apply(libs.findPlugin(id).get().get().pluginId)
            }
        }

        val composeDeps = extensions.getByType<ComposeExtension>().dependencies

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                commonMain {
                    dependencies {
                        implementation(composeDeps.runtime)
                        implementation(composeDeps.foundation)
                        implementation("org.jetbrains.compose.material3:material3:1.9.0-alpha04")
                        implementation(composeDeps.ui)
                        implementation(composeDeps.materialIconsExtended)
                        implementation(composeDeps.components.resources)
                        implementation(composeDeps.components.uiToolingPreview)
                    }
                }
                androidMain.dependencies {
                    implementation(composeDeps.preview)
                    implementation(libs.findBundle("preview").get())
                }
                jvmMain.dependencies {
                    implementation(composeDeps.desktop.currentOs)
                }
            }
        }
    }
}