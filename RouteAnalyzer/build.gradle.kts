plugins {
    kotlin("jvm") version "2.0.20"
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
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "it.polito.wa2.g01.MainKt"
}