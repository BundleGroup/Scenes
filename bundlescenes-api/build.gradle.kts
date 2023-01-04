plugins {
    id("bundlescenes.base-conventions")
    id("maven-publish")
}

dependencies {
    compileOnlyApi("org.jetbrains:annotations:23.1.0")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        val snapshotUrl = "https://repo.bundlegroup.gg/repository/maven-snapshots/"
        val releaseUrl = "https://repo.bundlegroup.gg/repository/maven-releases/"
        maven(if (version.toString().endsWith("-SNAPSHOT")) snapshotUrl else releaseUrl) {
            name = "bundlegroup"
            credentials(PasswordCredentials::class)
        }
    }
}
