plugins {
    id("bundlescenes.base")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(project(":bundlescenes"))
    compileOnly(libs.worldedit.bukkit)
}
