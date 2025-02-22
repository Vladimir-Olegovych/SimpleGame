package di

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import eventbus.GameEventBus
import org.koin.dsl.module

val appModule = module {
    val camera = OrthographicCamera(400F, 400F)
    val viewport = ScreenViewport(camera)

    single { GameEventBus() }
    single { Stage(viewport) }
    single { AssetManager() }
    single { SpriteBatch() }
    single { ShapeRenderer() }

}