package app.di

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import core.models.settings.ClientPreference
import event.GamePacket
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.dsl.module
import tools.eventbus.EventBus
import tools.graphics.viewport.FairViewport
import tools.graphics.viewport.UnfairViewport
import tools.kyro.client.GameClient
import tools.preference.JsonPreference
import java.util.concurrent.Executor

class GameViewport(size: Float, camera: OrthographicCamera): FairViewport(size, camera)

class UiViewport(size: Float): UnfairViewport(size)

val appModule = module {
    single<JsonPreference<ClientPreference>> {
        JsonPreference("client", ClientPreference())
    }

    single<ClientPreference> {
        get<JsonPreference<ClientPreference>>().getPreference()
    }

    single<SpriteBatch> { SpriteBatch() }

    single<AssetManager> { AssetManager() }

    single<EventBus> { EventBus() }

    single<GameClient<GamePacket>> {
        val gameClient = GameClient<GamePacket>()
        val executor = Executor { runnable -> Gdx.app.postRunnable(runnable) }
        gameClient.setCustomDispatcher(executor.asCoroutineDispatcher())
        gameClient
    }

    single<OrthographicCamera> { OrthographicCamera() }

    single<GameViewport> {
        val clientPreference: ClientPreference = get()
        val camera: OrthographicCamera = get()
        GameViewport(
            size = 30F,
            camera = camera
        )
    }

    single<UiViewport> {
        UiViewport(size = 700F)
    }

    single<Stage> {
        val uiViewport: UiViewport = get()
        Stage(uiViewport)
    }
}