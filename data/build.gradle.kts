plugins {
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
    api("io.insert-koin:koin-core:4.0.2")

    testImplementation("io.insert-koin:koin-test:4.0.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}