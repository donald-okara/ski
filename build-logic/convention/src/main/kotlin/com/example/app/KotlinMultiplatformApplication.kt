package com.example.app

import com.android.build.api.dsl.ApplicationExtension
import com.example.app.extensions.configureKotlinAndroid
import com.example.app.extensions.configureKotlinMultiplatform
import com.example.app.extensions.configureProjectDependencies
import com.example.app.extensions.coreModules
import com.example.app.extensions.datasourceModules
import com.example.app.extensions.segmentModules
import com.example.app.extensions.libs
import com.example.app.extensions.sharedModules
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformApplication: Plugin<Project> {
    override fun apply(target: Project):Unit = with(target){
        with(pluginManager){
            listOf(
                "kotlinMultiplatform",
                "androidApplication",
                "navigation3Convention",
                "composeMultiplatformPlugin",
                "koinConvention"
            ).forEach { id ->
                pluginManager.apply(libs.findPlugin(id).get().get().pluginId)
            }
        }

        configureProjectDependencies(segmentModules.all, sharedModules.all, coreModules.all, datasourceModules.all)
        extensions.configure<KotlinMultiplatformExtension>(::configureKotlinMultiplatform)
        extensions.configure<ApplicationExtension>(::configureKotlinAndroid)
    }
}