package di.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.Viewport
import dagger.Module
import dagger.Provides
import event.GamePacket
import kotlinx.coroutines.asCoroutineDispatcher
import tools.eventbus.EventBus
import tools.graphics.viewport.CycleViewportProcessor
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
    fun provideCycleViewportProcessor(): CycleViewportProcessor {
        return CycleViewportProcessor()
    }

    @Provides
    @Singleton
    fun provideOrthographicCamera(): OrthographicCamera = OrthographicCamera(30F, 16F)

    @Provides
    @Singleton
    fun provideViewport(cycleViewportProcessor: CycleViewportProcessor, camera: OrthographicCamera): Viewport {
        val viewport = FillViewport(30F, 16F, camera)
        cycleViewportProcessor.addViewport(viewport)
        return viewport
    }

    @Provides
    @Singleton
    fun provideStage(cycleViewportProcessor: CycleViewportProcessor): Stage {
        val camera = OrthographicCamera(30F, 16F)
        val viewport = FillViewport(30F, 16F, camera)
        cycleViewportProcessor.addViewport(viewport)
        return Stage(viewport)
    }
}