package ke.don.ski

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import ke.don.ski.extensions.configureKotlinAndroid
import ke.don.ski.extensions.configureKotlinMultiplatform
import ke.don.ski.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformLibrary: Plugin<Project> {
    override fun apply(target: Project):Unit = with(target){
        with(pluginManager){
            listOf(
                "androidLibrary",
                "kotlinMultiplatform",
            ).forEach { id ->
                pluginManager.apply(libs.findPlugin(id).get().get().pluginId)
            }
        }

        extensions.configure<KotlinMultiplatformExtension>(::configureKotlinMultiplatform)
        extensions.configure<LibraryExtension>(::configureKotlinAndroid)
    }
}