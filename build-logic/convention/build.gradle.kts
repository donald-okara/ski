plugins {
    `kotlin-dsl`
}
val moduleName = "com.example.app" //your module name
group = "$moduleName.buildlogic" //your module name
gradlePlugin {
    plugins {
        listOf(
            "KotlinMultiplatformLibrary",
            "KotlinMultiplatformApplication",
            "ComposeMultiplatformPlugin",
            "FeatureConvention",
            "DatasourceConvention",
            "Navigation3Convention",
            "KoinConvention"
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
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}
