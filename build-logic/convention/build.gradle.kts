plugins {
    `kotlin-dsl`
}
val moduleName = "ke.don.ski" //your module name
group = "$moduleName.buildlogic" //your module name
gradlePlugin {
    plugins {
        listOf(
            "KotlinMultiplatformLibrary",
            "KotlinMultiplatformApplication",
            "ComposeMultiplatformPlugin",
            "SegmentConvention",
        ).forEach { pluginName ->
            val pluginId = pluginName.replaceFirstChar { it.lowercase() }
            register(pluginId) {
                id = "$moduleName.$pluginId"
                implementationClass = "$moduleName.$pluginName"
            }
        }
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}
