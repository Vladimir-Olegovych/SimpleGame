plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
tasks{
    shadowJar {
        manifest {
            attributes["Main-Class"] = "org.example.ServerKt"
        }
    }
}

dependencies {
    implementation(kotlin("reflect"))

    api(project(":core"))
    implementation(files("/home/vladimir/Documents/Projects/Kotlin/Game-v1-library/build/libs/Game-v1-library-1.0-SNAPSHOT-all.jar"))
    implementation(files("../libs/GameServerUtils.jar"))
    implementation(files("../libs/CommonUtils.jar"))

    api("com.badlogicgames.gdx:gdx:1.13.1")
    api("com.badlogicgames.gdx:gdx-box2d:1.13.1")
    api("com.badlogicgames.gdx:gdx-freetype:1.13.1")

    api("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.13.1")
    api("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-desktop")
    api("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-desktop")
    api("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-desktop")

    api("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    api("com.fasterxml.jackson.core:jackson-core:2.14.2")
    api("com.fasterxml.jackson.core:jackson-annotations:2.14.2")

    api("net.onedaybeard.artemis:artemis-odb:2.3.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    api("com.esotericsoftware:kryonet:2.22.0-RC1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}