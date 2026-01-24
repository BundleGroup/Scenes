import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("scenes.base")
    alias(libs.plugins.shadow)
    alias(libs.plugins.hangar.publish)
    alias(libs.plugins.minotaur)
}

val supportedGameVersions = listOf("1.21.11")

dependencies {
    compileOnly(libs.paper.api)
    implementation(project(":scenes"))
    implementation(project(":scenes-traincarts"))
    implementation(project(":scenes-worldedit"))
    implementation(project(":scenes-worldguard"))
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filesMatching("*.yml") {
            expand(props)
        }
    }

    jar {
        archiveBaseName = "Scenes"
        archiveClassifier = "dev"
    }

    shadowJar {
        val prefix = "gg.bundlegroup.scenes.lib"
        relocate("io.leangen.geantyref", "$prefix.geantyref")
        relocate("org.incendo.cloud", "$prefix.cloud")
        mergeServiceFiles()
        archiveBaseName = "Scenes"
        archiveClassifier = ""
    }

    val staticJar by registering(Copy::class) {
        from(shadowJar)
        into(layout.buildDirectory.dir("static"))
        rename { "Scenes.jar" }
    }

    assemble {
        dependsOn(staticJar)
    }
}

modrinth {
    projectId = "scenes"
    uploadFile.set(tasks.shadowJar)
    versionType = "release"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    syncBodyFrom = provider { rootProject.file("README.md").readText() }
    gameVersions = supportedGameVersions
    loaders = listOf("paper")
}

hangarPublish {
    publications.register("plugin") {
        id = "Scenes"
        channel = "Release"
        version = project.version.toString()
        changelog = provider { rootProject.file("CHANGELOG.md").readText() }
        apiKey = System.getenv("HANGAR_API_TOKEN")
        platforms {
            register(Platforms.PAPER) {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = supportedGameVersions
            }
        }
        pages {
            resourcePage(provider { rootProject.file("README.md").readText() })
        }
    }
}
