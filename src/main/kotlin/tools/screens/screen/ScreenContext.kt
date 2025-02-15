package tools.screens.screen

import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.Disposable

abstract class ScreenContext: Disposable {

    abstract fun onAttached()
    abstract fun onDetached()

    abstract fun onPause()
    abstract fun onResume()

    abstract fun onRender(deltaTime: Float)
    abstract fun onResize(width: Int, height: Int)

    val screen = object : Screen {
        override fun show() {}
        override fun render(deltaTime: Float) { onRender(deltaTime) }
        override fun resize(width: Int, height: Int) { onResize(width, height) }
        override fun pause() { onPause() }
        override fun resume() { onResume() }
        override fun hide() {}
        override fun dispose() {}
    }

    override fun dispose() {
        screen.dispose()
    }

}