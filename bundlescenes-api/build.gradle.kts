plugins {
    id("bundlescenes.base")
    id("maven-publish")
}

dependencies {
    compileOnly(libs.paper.api)
}

publishing {
    repositories {
        maven("https://repo.theatlas.gg/repository/maven-snapshots/") {
            name = "atlas"
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
