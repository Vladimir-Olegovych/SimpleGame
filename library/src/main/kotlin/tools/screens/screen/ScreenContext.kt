package tools.screens.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor

abstract class ScreenContext: Disposable, Executor {

    val lifecycleScope = CoroutineScope(this.asCoroutineDispatcher()  + SupervisorJob())

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

    override fun execute(runnble: Runnable) {
        Gdx.app.postRunnable { runnble.run() }
    }

    override fun dispose() {
        screen.dispose()
    }

}