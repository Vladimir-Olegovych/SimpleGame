plugins {
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("/home/vladimir/Documents/Projects/kotlin/Game-v1-library/build/libs/Game-v1-library-1.0-SNAPSHOT-all.jar"))

    api(project(":core"))

    api("com.badlogicgames.gdx:gdx:1.13.1")
    api("com.badlogicgames.gdx:gdx-box2d:1.13.1")
    api("com.badlogicgames.gdx:gdx-freetype:1.13.1")

    api("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.13.1")
    api("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-desktop")
    api("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-desktop")
    api("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-desktop")

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