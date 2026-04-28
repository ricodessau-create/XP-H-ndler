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
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Feste Version – KEIN SNAPSHOT – garantiert PlayerDisplay
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-20241019.012345-123")

    compileOnly(files("libs/ProtocolLib.jar"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.jar {
    archiveBaseName.set("BierXP")
}
