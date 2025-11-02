package di.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import dagger.Module
import dagger.Provides
import event.GamePacket
import kotlinx.coroutines.asCoroutineDispatcher
import tools.eventbus.EventBus
import tools.kyro.client.GameClient
import java.util.concurrent.Executor
import javax.inject.Singleton

class GameViewport(camera: OrthographicCamera): FillViewport(30F, 16F, camera)
class UiViewport(): ScreenViewport()

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideSpriteBatch(): SpriteBatch = SpriteBatch()

    @Provides
    @Singleton
    fun provideAssetManager(): AssetManager = AssetManager()

    @Provides
    @Singleton
    fun provideEventBus(): EventBus = EventBus()

    @Provides
    @Singleton
    fun provideGameClient(): GameClient<GamePacket> {
        val gameClient = GameClient<GamePacket>()
        val executor = Executor { runnable -> Gdx.app.postRunnable(runnable) }
        gameClient.setCustomDispatcher(executor.asCoroutineDispatcher())
        return gameClient
    }


    @Provides
    @Singleton
    fun provideOrthographicCamera(): OrthographicCamera = OrthographicCamera(30F, 16F)

    @Provides
    @Singleton
    fun provideGameViewport(camera: OrthographicCamera): GameViewport {
        val viewport = GameViewport(camera)
        return viewport
    }

    @Provides
    @Singleton
    fun provideUiViewport(): UiViewport {
        val viewport = UiViewport()
        return viewport
    }

    @Provides
    @Singleton
    fun provideStage(uiViewport: UiViewport): Stage {
        return Stage(uiViewport)
    }
}