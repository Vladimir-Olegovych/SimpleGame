package di.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import core.models.ClientPreference
import dagger.Module
import dagger.Provides
import event.GamePacket
import kotlinx.coroutines.asCoroutineDispatcher
import tools.eventbus.EventBus
import tools.kyro.client.GameClient
import tools.preference.JsonPreference
import java.util.concurrent.Executor
import javax.inject.Singleton

class GameViewport(worldWidth: Float,
                   worldHeight: Float,
                   camera: OrthographicCamera
): FillViewport(worldWidth, worldHeight, camera)
class UiViewport(): ScreenViewport()

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideJsonPreference(): JsonPreference<ClientPreference> {
        return JsonPreference("client", ClientPreference())
    }

    @Provides
    @Singleton
    fun provideClientPreference(jsonPreference: JsonPreference<ClientPreference>): ClientPreference {
        return jsonPreference.getPreference()
    }

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
    fun provideOrthographicCamera(): OrthographicCamera {
        return OrthographicCamera()
    }

    @Provides
    @Singleton
    fun provideGameViewport(clientPreference: ClientPreference, camera: OrthographicCamera): GameViewport {
        val viewport = GameViewport(
            worldWidth = 30F * clientPreference.drawScale,
            worldHeight = 16F * clientPreference.drawScale,
            camera = camera
        )
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