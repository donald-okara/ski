plugins {
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatformPlugin)
    alias(libs.plugins.maven.publishing)
}

android {
    namespace = "io.github.donald_okara"
}

group = "io.github.donald-okara"
version = project.findProperty("version") ?: throw GradleException("Version property is required. Pass it with -Pversion=<version>")

mavenPublishing {
    publishToMavenCentral() // or publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(group as String, "ski", version as String)

    pom {
        name.set("Ski components library")
        description.set("The components for the Ski slides template")
        inceptionYear.set("2025")
        url.set("https://github.com/donald-okara/ski/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("donald-okara")
                name.set("Donald Okara")
                url.set("https://github.com/donald-okara/")
            }
        }
        scm {
            url.set("https://github.com/donald-okara/ski/")
            connection.set("scm:git:git://github.com/donald-okara/ski.git")
            developerConnection.set("scm:git:ssh://git@github.com/donald-okara/ski.git")
        }
    }
}

// ─── Dynamically infer tag version ─────────────────────────────────────────────

val gitTagVersion: String by lazy {
    try {
        "git describe --tags --abbrev=0".runCommand()
    } catch (e: Exception) {
        "untagged"
    }
}

fun String.runCommand(): String =
    ProcessBuilder(*split(" ").toTypedArray())
        .directory(rootDir)
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()