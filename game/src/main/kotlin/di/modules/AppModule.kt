package di.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.Viewport
import dagger.Module
import dagger.Provides
import eventbus.GameEventBus
import kotlinx.coroutines.asCoroutineDispatcher
import model.GamePaket
import tools.kyro.client.GameClient
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideSpriteBatch(): SpriteBatch = SpriteBatch()

    @Provides
    @Singleton
    fun provideShapeRender(): ShapeRenderer = ShapeRenderer()

    @Provides
    @Singleton
    fun provideGameClient(): GameClient<GamePaket> {
        val gameClient = GameClient<GamePaket>()
        val executor = Executor { runnable -> Gdx.app.postRunnable(runnable) }
        gameClient.setCustomDispatcher(executor.asCoroutineDispatcher())
        return gameClient
    }

    @Provides
    @Singleton
    fun provideEventBus(gameClient: GameClient<GamePaket>): GameEventBus {
        val eventBus = GameEventBus()
        gameClient.subscribe(eventBus.getListener())
        return eventBus
    }

    @Provides
    @Singleton
    fun provideOrthographicCamera(): OrthographicCamera = OrthographicCamera(10F, 10F)

    @Provides
    @Singleton
    fun provideViewport(camera: OrthographicCamera): Viewport = FillViewport(20F, 20F, camera)

    @Provides
    @Singleton
    fun provideStage(viewport: Viewport): Stage = Stage(viewport)

    @Provides
    @Singleton
    fun provideAssetManager(): AssetManager = AssetManager()
}