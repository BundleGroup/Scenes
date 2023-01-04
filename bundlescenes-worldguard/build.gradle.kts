plugins {
    id("bundlescenes.base-conventions")
}

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    implementation(project(":bundlescenes"))
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
}
