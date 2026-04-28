plugins {
    java
}

group = "de.bierxp"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()

    // WICHTIG: Paper 1.21.4 liegt NUR hier
    maven("https://repo.papermc.io/repository/maven-snapshots/")
}

dependencies {
    // PlayerDisplay existiert NUR in dieser Version
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    compileOnly(files("libs/ProtocolLib.jar"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.jar {
    archiveBaseName.set("BierXP")
}
