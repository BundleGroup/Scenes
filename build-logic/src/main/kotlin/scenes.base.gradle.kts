plugins {
    id("java-library")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://ci.mg-dev.eu/plugin/repository/everything/")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    components {
        listOf(
            "com.sk89q.worldguard:worldguard-bukkit",
            "com.sk89q.worldguard:worldguard-core",
            "com.sk89q.worldedit:worldedit-bukkit",
            "com.sk89q.worldedit:worldedit-core",
        ).forEach { module ->
            withModule(module) {
                allVariants {
                    withDependencyConstraints {
                        // Paper 26.2 ships newer platform libraries than EngineHub's "Mojang provides" pins.
                        removeAll {
                            it.group == "com.google.guava" ||
                                it.group == "com.google.code.gson" ||
                                it.group == "org.apache.logging.log4j"
                        }
                    }
                }
            }
        }
    }
}
