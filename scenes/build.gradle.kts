plugins {
    id("scenes.base")
}

dependencies {
    compileOnly(libs.paper.api)
    api(project(":scenes-api"))
}
