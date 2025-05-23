package org.example

import app.GameApplication
import com.badlogic.gdx.Game
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    val gameApplication = GameApplication()
    startScreen(gameApplication)
}

private fun startScreen(game: Game){
    val config = Lwjgl3ApplicationConfiguration()
    config.useVsync(false)
    config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate)
    config.setIdleFPS(30)
    config.setTitle("Amogus")
    //config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode())
    Lwjgl3Application(game, config)
}