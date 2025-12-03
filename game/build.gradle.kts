plugins {
    kotlin("jvm") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    implementation(files("/home/vladimir/Documents/Projects/Kotlin/Game-v1-library/build/libs/Game-v1-library-1.0-SNAPSHOT-all.jar"))
    api(project(":core"))

    api("com.badlogicgames.gdx:gdx-freetype:1.13.1")
    api("com.badlogicgames.gdx:gdx:1.13.1")

    api("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    api("com.fasterxml.jackson.core:jackson-core:2.14.2")
    api("com.fasterxml.jackson.core:jackson-annotations:2.14.2")

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    api("net.onedaybeard.artemis:artemis-odb:2.3.0")
    api("com.esotericsoftware:kryonet:2.22.0-RC1")

    implementation("io.insert-koin:koin-core:3.5.6")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}