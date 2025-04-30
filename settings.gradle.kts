plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "GameJora"
include("game")
include("desktop")
include("core")
include("server")

