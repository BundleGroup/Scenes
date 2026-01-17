plugins {
    id("java-library")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
