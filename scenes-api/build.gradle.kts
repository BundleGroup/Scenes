plugins {
    id("scenes.base")
    id("maven-publish")
}

dependencies {
    compileOnly(libs.paper.api)
}

publishing {
    repositories {
        maven("https://repo.bundlegroup.gg/repository/maven-snapshots/") {
            name = "bundlegroup"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}
