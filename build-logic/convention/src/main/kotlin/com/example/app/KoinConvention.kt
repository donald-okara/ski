package com.example.app

import com.example.app.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KoinConvention: Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                commonMain.dependencies {
                    implementation(libs.findBundle("koin-common").get())
                }
                androidMain.dependencies {
                    implementation(libs.findBundle("koin-android").get())
                }
            }
        }
    }
}