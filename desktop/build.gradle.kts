plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"
tasks{
    shadowJar {
        manifest {
            attributes["Main-Class"] = "org.example.GameKt"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {

    api(project(":game"))
    api("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-desktop")
    api("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-desktop")
    api("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-desktop")
    api("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.13.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}