package org.example

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import game.MainGame

fun main() {
    val config = Lwjgl3ApplicationConfiguration()
    config.useVsync(false)
    config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate)
    config.setIdleFPS(30)
    config.setTitle("Amogus")
    //config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode())
    Lwjgl3Application(MainGame(), config)
}