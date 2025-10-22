package app.screens

import app.navigation.Navigation
import com.artemis.World
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import ecs.components.Player
import ecs.processors.ClientProcessor
import ecs.systems.DrawSystem
import ecs.systems.EntitySystem
import ecs.systems.InputSystem
import core.eventbus.GameEventBus
import event.GamePacket
import tools.artemis.world.ArtemisWorldBuilder
import tools.graphics.input.CycleInputProcessor
import tools.graphics.screens.fragment.Fragment
import tools.kyro.client.GameClient
import utils.registerAllEvents
import javax.inject.Inject


class GameFragment(
    private val navigation: Navigation.Game,
    private val onDisconnect: () -> Unit
): Fragment() {

    @Inject lateinit var gameClient: GameClient<GamePacket>
    @Inject lateinit var assetManager: AssetManager
    @Inject lateinit var shapeRenderer: ShapeRenderer
    @Inject lateinit var spriteBatch: SpriteBatch
    @Inject lateinit var camera: OrthographicCamera
    @Inject lateinit var viewport: Viewport
    @Inject lateinit var eventBus: GameEventBus

    private lateinit var artemisWorld: World
    private val inputProcessor = CycleInputProcessor()


    override fun onCreate(game: Game) {

        val entitySystem = EntitySystem()

        val clientProcessor = ClientProcessor(
            entitySystem = entitySystem,
            onDisconnect = onDisconnect
        )

        val inputSystem = InputSystem()
        val drawSystem = DrawSystem()

        eventBus.addHandler(clientProcessor)

        inputProcessor.addProcessor(inputSystem)
        artemisWorld = ArtemisWorldBuilder()
            .addSystem(inputSystem)
            .addSystem(drawSystem)
            .addSystem(entitySystem)
            .addObject(Player())
            .addObject(gameClient)
            .addObject(shapeRenderer)
            .addObject(spriteBatch)
            .addObject(camera)
            .addObject(assetManager)
            .build()

        clientProcessor.create(artemisWorld)

        Gdx.input.inputProcessor = inputProcessor

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
        viewport.update(width, height)
    }

    override fun onDestroy() {
        eventBus.clearHandlers()
        Gdx.input.inputProcessor = null
        inputProcessor.clear()
        artemisWorld.dispose()
    }
}