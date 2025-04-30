plugins {
    kotlin("jvm") version "2.1.10"
    id("kotlin-kapt")
}

group = "org.example"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation(files("/home/vladimir/Documents/Projects/kotlin/Game-v1-library/build/libs/Game-v1-library-1.0-SNAPSHOT-all.jar"))
    api(project(":core"))

    api("com.badlogicgames.gdx:gdx-freetype:1.13.1")
    api("com.badlogicgames.gdx:gdx:1.13.1")

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    api("net.onedaybeard.artemis:artemis-odb:2.3.0")
    api("com.esotericsoftware:kryonet:2.22.0-RC1")

    implementation("com.google.dagger:dagger:2.56.2")
    kapt("com.google.dagger:dagger-compiler:2.56.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}