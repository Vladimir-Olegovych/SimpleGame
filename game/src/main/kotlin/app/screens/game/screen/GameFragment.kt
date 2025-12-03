package app.screens.game.screen

import app.di.GameViewport
import app.di.UiViewport
import app.ecs.models.Player
import app.ecs.models.SendEvents
import app.ecs.processors.ServerInputProcessor
import app.ecs.systems.*
import app.navigation.Navigation
import app.screens.game.dialog.MenuDialog
import com.artemis.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import core.models.settings.ClientPreference
import event.GamePacket
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tools.artemis.world.ArtemisWorldBuilder
import tools.eventbus.EventBus
import tools.graphics.input.CycleInputProcessor
import tools.graphics.screens.fragment.Fragment
import tools.kyro.client.GameClient
import utils.registerAllEvents

class GameFragment(
    private val navigation: Navigation.Game,
    private val onBack: () -> Unit
): KoinComponent, Fragment() {

    private val clientPreference: ClientPreference by inject()
    private val gameClient: GameClient<GamePacket> by inject()
    private val eventBus: EventBus by inject()
    private val assetManager: AssetManager by inject()
    private val spriteBatch: SpriteBatch by inject()
    private val camera: OrthographicCamera by inject()
    private val stage: Stage by inject()
    private val gameViewport: GameViewport by inject()
    private val uiViewport: UiViewport by inject()

    private lateinit var artemisWorld: World
    private val inputProcessor = CycleInputProcessor()

    override fun onCreate() {
        val entitySystem = EntitySystem()
        val player = Player()
        val sendEvents = SendEvents()

        val worldBuilder = ArtemisWorldBuilder()

        val menuDialog = MenuDialog(stage, assetManager, onQuit = onBack)

        worldBuilder
            .addSystem(entitySystem)
            .addSystem(DrawSystem())
            .addSystem(InputSystem())
            .addSystem(UiSystem())
            .addSystem(ServerSystem())
            .addObject(player)
            .addObject(sendEvents)
            .addObject(menuDialog)
            .addObject(inputProcessor)
            .addObject(dialogManager)
            .addObject(clientPreference)
            .addObject(gameViewport)
            .addObject(uiViewport)
            .addObject(stage)
            .addObject(gameClient)
            .addObject(spriteBatch)
            .addObject(camera)
            .addObject(assetManager)

        artemisWorld = worldBuilder.build()

        val serverInputProcessor = ServerInputProcessor(
            eventBus = eventBus,
            onDisconnect = onBack
        )

        eventBus.registerHandler(entitySystem)

        Gdx.input.inputProcessor = inputProcessor
        gameClient.subscribe(serverInputProcessor)

        gameClient.start(
            address = "127.0.0.1",
            port = 5000,
            bufferSize = 262144,
            custom = { kryo ->
                kryo.registerAllEvents()
            }
        )
    }

    override fun onRender(deltaTime: Float) {
        try {
            eventBus.process()
            artemisWorld.delta = deltaTime
            artemisWorld.process()
        } catch (e: Throwable){
            e.printStackTrace()
        }
    }

    override fun onResize(width: Int, height: Int) {
        gameViewport.update(width, height, false)
        uiViewport.update(width, height, true)
    }

    override fun onDestroy() {
        Gdx.input.inputProcessor = null
        stage.clear()

        eventBus.clear()
        gameClient.stop()
        gameClient.unSubscribeAll()

        inputProcessor.clear()
        artemisWorld.dispose()
    }

}