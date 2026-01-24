plugins {
    id("scenes.base")
    id("maven-publish")
}

dependencies {
    compileOnly(libs.paper.api)
}

publishing {
    repositories {
        if (version.toString().endsWith("-SNAPSHOT")) {
            maven("https://repo.bundlegroup.gg/repository/maven-snapshots/") {
                name = "bundlegroup"
                credentials(PasswordCredentials::class)
            }
        } else {
            maven("https://repo.bundlegroup.gg/repository/maven-releases/") {
                name = "bundlegroup"
                credentials(PasswordCredentials::class)
            }
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
