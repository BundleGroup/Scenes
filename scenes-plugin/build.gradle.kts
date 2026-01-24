plugins {
    id("scenes.base")
    alias(libs.plugins.shadow)
}

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

    shadowJar {
        val prefix = "gg.bundlegroup.scenes.lib"
        relocate("io.leangen.geantyref", "$prefix.geantyref")
        relocate("org.incendo.cloud", "$prefix.cloud")
        mergeServiceFiles()
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
