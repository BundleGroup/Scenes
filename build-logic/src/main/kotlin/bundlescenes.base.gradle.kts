plugins {
    id("java-library")
}

repositories {
    maven("https://repo.bundlegroup.gg/repository/maven-public/") {
        name = "bundlegroup"
        credentials(PasswordCredentials::class)
    }
    maven("https://maven.enginehub.org/repo/")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
