plugins {
    id("bundlescenes.base")
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.paper.api)
    implementation(project(":bundlescenes"))
    implementation(project(":bundlescenes-traincarts"))
    implementation(project(":bundlescenes-worldedit"))
    implementation(project(":bundlescenes-worldguard"))
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
