package ke.don.ski.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val Project.moduleName: String
    get() = path.split(":").drop(1).joinToString(".")

fun Project.configureProjectDependencies(vararg moduleLists: List<Project>) {
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.apply {
            commonMain.dependencies {
                moduleLists.flatMap { it }.forEach { implementation(it) }
            }
        }
    }
}

val Project.coreModules: CoreModules
    get() = CoreModules(this)

class CoreModules(private val project: Project) {
    val domain get() = project.project(":core:domain")

    val all get() = listOf(domain)
}

val Project.segmentModules: SegmentModules
    get() = SegmentModules(this)

class SegmentModules(private val project: Project) {
    val demos get() = project.project(":segments:demos")
    val introduction get() = project.project(":segments:introduction")

    val all: List<Project> = listOf(demos, introduction)
}

val Project.sharedModules: SharedModules
    get() = SharedModules(this)

class SharedModules(private val project: Project) {
    val design get() = project.project(":shared:design")
    val resources get() = project.project(":shared:resources")

    val all get() = listOf(design, resources)
}