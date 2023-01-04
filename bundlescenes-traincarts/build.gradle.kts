plugins {
    id("bundlescenes.base-conventions")
}

repositories {
    maven("https://ci.mg-dev.eu/plugin/repository/everything/") {
        content {
            includeGroupByRegex("com\\.bergerkiller\\..*")
        }
    }
}

dependencies {
    implementation(project(":bundlescenes"))
    compileOnly("com.bergerkiller.bukkit:TrainCarts:1.19.2-v1")
}
