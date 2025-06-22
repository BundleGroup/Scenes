plugins {
    id("bundlescenes.base")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnlyApi(libs.bundleentities.api)
}
