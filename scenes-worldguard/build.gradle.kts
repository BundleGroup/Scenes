plugins {
    id("scenes.base")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(project(":scenes"))
    compileOnlyApi(libs.worldguard.bukkit)
}
