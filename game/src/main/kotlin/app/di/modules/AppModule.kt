package app.di.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import core.models.settings.ClientPreference
import dagger.Module
import dagger.Provides
import event.GamePacket
import kotlinx.coroutines.asCoroutineDispatcher
import tools.eventbus.EventBus
import tools.graphics.viewport.FairViewport
import tools.graphics.viewport.UnfairViewport
import tools.kyro.client.GameClient
import tools.preference.JsonPreference
import java.util.concurrent.Executor
import javax.inject.Singleton

class GameViewport(size: Float, camera: OrthographicCamera): FairViewport(size, camera)

class UiViewport(size: Float): UnfairViewport(size)

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
            size = 30F * clientPreference.drawScale,
            camera = camera
        )
        return viewport
    }

    @Provides
    @Singleton
    fun provideUiViewport(clientPreference: ClientPreference): UiViewport {
        val viewport = UiViewport(
            size = 16F * clientPreference.drawScale,
        )
        return viewport
    }

    @Provides
    @Singleton
    fun provideStage(uiViewport: UiViewport): Stage {
        return Stage(uiViewport)
    }
}