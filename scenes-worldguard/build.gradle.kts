plugins {
    id("scenes.base-conventions")
}

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    implementation(project(":scenes"))
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
}
