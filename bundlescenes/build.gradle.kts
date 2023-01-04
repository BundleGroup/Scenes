plugins {
    id("bundlescenes.base-conventions")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

bukkit {
    name = "BundleScenes"
    main = "gg.bundlegroup.bundlescenes.plugin.ScenesPlugin"
    apiVersion = "1.19"
    author = "56738"
    softDepend = listOf("Train_Carts", "WorldGuard")
}

val cloudVersion = "1.8.0"

dependencies {
    api(project(":bundlescenes-api"))
    runtimeOnly(project(":bundlescenes-entity"))
    runtimeOnly(project(":bundlescenes-traincarts"))
    runtimeOnly(project(":bundlescenes-worldguard"))
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
            relocate(pkg, "gg.bundlegroup.bundlescenes.lib.$pkg")
        }
    }
}
