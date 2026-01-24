plugins {
    id("scenes.base")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(project(":scenes"))
    compileOnly(libs.traincarts)
}
