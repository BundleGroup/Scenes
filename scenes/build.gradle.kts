plugins {
    id("scenes.base-conventions")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

bukkit {
    name = "Scenes"
    main = "gg.bundlegroup.scenes.plugin.ScenesPlugin"
    apiVersion = "1.19"
    author = "56738"
    softDepend = listOf("Train_Carts")
}

val cloudVersion = "1.8.0"

dependencies {
    api(project(":scenes-api"))
    runtimeOnly(project(":scenes-entity"))
    runtimeOnly(project(":scenes-traincarts"))
    api("net.kyori:adventure-platform-bukkit:4.2.0")
    api("cloud.commandframework:cloud-paper:$cloudVersion")
    api("cloud.commandframework:cloud-annotations:$cloudVersion")
    api("cloud.commandframework:cloud-minecraft-extras:$cloudVersion") {
        exclude("net.kyori")
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        for (pkg in listOf(
            "cloud.commandframework",
            "io.leangen.geantyref",
            "net.kyori"
        )) {
            relocate(pkg, "gg.bundlegroup.scenes.lib.$pkg")
        }
    }
}
