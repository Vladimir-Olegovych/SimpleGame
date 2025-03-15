package di

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import eventbus.GameEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.dsl.module
import java.util.concurrent.Executor

val appModule = module {
    val scope = CoroutineScope(Executor { runnable -> Gdx.app.postRunnable(runnable) }.asCoroutineDispatcher())
    val camera = OrthographicCamera(100F, 100F)
    val viewport = FillViewport(100F, 100F, camera)

    single { GameEventBus(scope) }
    single { Stage(viewport) }
    single { AssetManager() }
    single { SpriteBatch() }
    single { ShapeRenderer() }

}