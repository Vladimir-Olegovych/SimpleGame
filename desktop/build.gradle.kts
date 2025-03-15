plugins {
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

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