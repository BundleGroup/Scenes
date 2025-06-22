plugins {
    id("bundlescenes.base")
    alias(libs.plugins.shadow)
}

dependencies {
    api(project(":bundlescenes-api"))
    compileOnly(libs.paper.api)
    implementation(libs.cloud.paper)
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
        val prefix = "gg.bundlegroup.bundlescenes.lib"
        relocate("io.leangen.geantyref", "$prefix.geantyref")
        relocate("org.incendo.cloud", "$prefix.cloud")
        mergeServiceFiles()
    }

    val staticJar by registering(Copy::class) {
        from(shadowJar)
        into(layout.buildDirectory.dir("static"))
        rename { "BundleScenes.jar" }
    }

    assemble {
        dependsOn(staticJar)
    }
}
