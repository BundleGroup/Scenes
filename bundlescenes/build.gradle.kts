plugins {
    id("bundlescenes.base-conventions")
//    id("net.minecrell.plugin-yml.paper") version "0.6.0"
//    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.gradleup.shadow") version "9.0.0-beta8"
}
//
//bukkit {
//    name = "BundleScenes"
//    main = "gg.bundlegroup.bundlescenes.plugin.ScenesPlugin"
//    apiVersion = "1.19"
//    author = "56738"
//    softDepend = listOf("Train_Carts", "WorldGuard")
//}

val lampVersion = "4.0.0-rc.8"

dependencies {
    api(project(":bundlescenes-api"))
    runtimeOnly(project(":bundlescenes-entity"))
//    runtimeOnly(project(":bundlescenes-traincarts"))
    runtimeOnly(project(":bundlescenes-worldguard"))
    api("net.kyori:adventure-platform-bukkit:4.2.0")
    api("io.github.revxrsal:lamp.common:$lampVersion")
    api("io.github.revxrsal:lamp.brigadier:4.0.0-beta.19")
    api("io.github.revxrsal:lamp.bukkit:4.0.0-beta.19")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        for (pkg in listOf(
            "io.github.revxrsal",
            "io.leangen.geantyref",
            "net.kyori"
        )) {
            relocate(pkg, "gg.bundlegroup.bundlescenes.lib.$pkg")
        }
    }
}
