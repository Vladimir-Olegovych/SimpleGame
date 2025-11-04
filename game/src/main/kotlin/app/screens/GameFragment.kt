package app.screens

import app.navigation.Navigation
import com.artemis.World
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import core.models.ClientPreference
import di.modules.GameViewport
import di.modules.UiViewport
import ecs.components.Player
import ecs.processors.ClientProcessor
import ecs.systems.DrawSystem
import ecs.systems.EntitySystem
import ecs.systems.InputSystem
import ecs.systems.UiSystem
import event.GamePacket
import tools.artemis.world.ArtemisWorldBuilder
import tools.eventbus.EventBus
import tools.graphics.input.CycleInputProcessor
import tools.graphics.screens.fragment.Fragment
import tools.kyro.client.GameClient
import utils.registerAllEvents
import javax.inject.Inject


class GameFragment(
    private val navigation: Navigation.Game,
    private val onDisconnect: () -> Unit
): Fragment() {

    @Inject lateinit var clientPreference: ClientPreference
    @Inject lateinit var gameClient: GameClient<GamePacket>
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var assetManager: AssetManager
    @Inject lateinit var spriteBatch: SpriteBatch
    @Inject lateinit var camera: OrthographicCamera
    @Inject lateinit var stage: Stage
    @Inject lateinit var gameViewport: GameViewport
    @Inject lateinit var uiViewport: UiViewport

    private lateinit var artemisWorld: World
    private val inputProcessor = CycleInputProcessor()

    override fun onCreate(game: Game) {
        val entitySystem = EntitySystem()
        val inputSystem = InputSystem()
        val drawSystem = DrawSystem()
        val uiSystem = UiSystem(
            onDisconnect = onDisconnect
        )

        inputProcessor.addProcessor(stage)
        inputProcessor.addProcessor(inputSystem)

        artemisWorld = ArtemisWorldBuilder()
            .addSystem(inputSystem)
            .addSystem(entitySystem)
            .addSystem(drawSystem)
            .addSystem(uiSystem)
            .addObject(Player())
            .addObject(clientPreference)
            .addObject(gameViewport)
            .addObject(uiViewport)
            .addObject(stage)
            .addObject(gameClient)
            .addObject(spriteBatch)
            .addObject(camera)
            .addObject(assetManager)
            .build()

        val clientProcessor = ClientProcessor(
            eventBus = eventBus,
            onDisconnect = onDisconnect
        )

        eventBus.registerHandler(entitySystem)

        Gdx.input.inputProcessor = inputProcessor
        gameClient.subscribe(clientProcessor)

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
        artemisWorld.delta = deltaTime
        artemisWorld.process()
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