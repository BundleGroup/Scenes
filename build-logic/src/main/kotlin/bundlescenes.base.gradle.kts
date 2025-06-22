plugins {
    id("java-library")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.theatlas.gg/repository/maven-public/") {
        name = "atlas"
        credentials(PasswordCredentials::class)
    }
    maven("https://maven.enginehub.org/repo/")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
