package ke.don.ski

import com.android.build.api.dsl.ApplicationExtension
import ke.don.ski.extensions.configureComponents
import ke.don.ski.extensions.configureKotlinAndroid
import ke.don.ski.extensions.configureKotlinMultiplatform
import ke.don.ski.extensions.configureProjectDependencies
import ke.don.ski.extensions.coreModules
import ke.don.ski.extensions.libs
import ke.don.ski.extensions.segmentModules
import ke.don.ski.extensions.sharedModules
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformApplication: Plugin<Project> {
    override fun apply(target: Project):Unit = with(target){
        with(pluginManager){
            listOf(
                "kotlinMultiplatform",
                "composeMultiplatformPlugin",
                "androidApplication"
            ).forEach { id ->
                pluginManager.apply(libs.findPlugin(id).get().get().pluginId)
            }
        }

        configureProjectDependencies(sharedModules.all, coreModules.all, segmentModules.all)
        extensions.configure<KotlinMultiplatformExtension>(::configureKotlinMultiplatform)
        extensions.configure<KotlinMultiplatformExtension>(::configureComponents)
        extensions.configure<ApplicationExtension>(::configureKotlinAndroid)
    }
}