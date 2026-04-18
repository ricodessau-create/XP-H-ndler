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

    // Purpur API
    maven("https://repo.purpurmc.org/snapshots")

    // Richtiges ProtocolLib Repository
    maven("https://repo.dmulloy2.net/repository/maven-public/")
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.2.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.jar {
    archiveBaseName.set("BierXP")
}
