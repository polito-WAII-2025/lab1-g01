plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    application
    id("com.gradleup.shadow") version "8.3.5"
}

group = "it.polito.wa2.g01"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.0")
    implementation("com.uber:h3:3.7.2")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "it.polito.wa2.g01.MainKt"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "it.polito.wa2.g01.MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}
