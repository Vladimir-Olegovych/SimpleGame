plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "Game-v1"
include("game")
include("server")
include("desktop")
include("core")

