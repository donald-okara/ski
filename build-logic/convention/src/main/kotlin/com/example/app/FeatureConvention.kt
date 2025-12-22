package com.example.app

import appIdentity
import com.example.app.extensions.configureProjectDependencies
import com.example.app.extensions.coreModules
import com.example.app.extensions.datasourceModules
import com.example.app.extensions.sharedModules
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FeatureConvention : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            listOf(
                "kotlinMultiplatformLibrary",
                "composeMultiplatformPlugin",
                "navigation3Convention",
                "koinConvention"
            ).forEach { id ->
                apply("${appIdentity.packageName}.$id")
            }
        }

        configureProjectDependencies(coreModules.all, datasourceModules.all, sharedModules.all)
    }
}
