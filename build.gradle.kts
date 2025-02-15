plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

tasks{
    shadowJar {
        manifest {
            attributes["Main-Class"] = "GameClientDesktop.src.main.kotlin.gigcreator.game.desktop.MainKt"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":data"))

    api("com.badlogicgames.gdx:gdx-freetype:1.13.1")
    api("com.badlogicgames.gdx:gdx-box2d:1.13.1")
    api("com.badlogicgames.gdx:gdx:1.13.1")

    api("net.onedaybeard.artemis:artemis-odb:2.3.0")
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