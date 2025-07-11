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
import ecs.systems.DrawSystem
import ecs.systems.EntitySystem
import ecs.systems.InputSystem
import eventbus.GameEventBus
import kotlinx.coroutines.launch
import model.Event
import tools.artemis.world.ArtemisWorldBuilder
import tools.graphics.input.CycleInputProcessor
import tools.graphics.screens.fragment.Fragment
import tools.kyro.client.GameClient
import utils.registerAllEvents
import javax.inject.Inject


class GameFragment(
    private val navigation: Navigation.Game,
    private val onDisconnected: () -> Unit
): Fragment() {

    @Inject lateinit var gameClient: GameClient<Event>
    @Inject lateinit var assetManager: AssetManager
    @Inject lateinit var shapeRenderer: ShapeRenderer
    @Inject lateinit var spriteBatch: SpriteBatch
    @Inject lateinit var camera: OrthographicCamera
    @Inject lateinit var viewport: Viewport
    @Inject lateinit var eventBus: GameEventBus

    private lateinit var artemisWorld: World
    private val inputProcessor = CycleInputProcessor()


    override fun onCreate(game: Game) {
        gameClient.subscribe(
            onConnected = { listener, connection ->

            },
            onDisconnected = { listener, connection ->
                lifecycleScope.launch {
                    gameClient.unSubscribe(listener)
                    onDisconnected.invoke()
                }
            }
        )

        val inputSystem = InputSystem()
        val drawSystem = DrawSystem()
        val entitySystem = EntitySystem()

        eventBus.addHandler(inputSystem)
        eventBus.addHandler(drawSystem)
        eventBus.addHandler(entitySystem)

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

        Gdx.input.inputProcessor = inputProcessor

        gameClient.start(
            address = "127.0.0.1",
            port = 5000,
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