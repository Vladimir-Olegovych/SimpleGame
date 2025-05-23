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
import ecs.systems.InputSystem
import ecs.systems.ServerSystem
import model.Event
import tools.artemis.world.ArtemisWorldBuilder
import tools.graphics.input.CycleInputProcessor
import tools.graphics.screens.fragment.Fragment
import tools.kyro.client.GameClient
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

    private lateinit var artemisWorld: World
    private val inputProcessor = CycleInputProcessor()


    override fun onCreate(game: Game) {
        val inputSystem = InputSystem()
        val drawSystem = DrawSystem()
        val serverSystem = ServerSystem(
            onDisconnected = {
                onDisconnected.invoke()
            }
        )

        inputProcessor.addProcessor(inputSystem)
        artemisWorld = ArtemisWorldBuilder()
            .addSystem(serverSystem)
            .addSystem(inputSystem)
            .addSystem(drawSystem)
            .addObject(gameClient)
            .addObject(shapeRenderer)
            .addObject(spriteBatch)
            .addObject(camera)
            .addObject(assetManager)
            .build()

        Gdx.input.inputProcessor = inputProcessor
    }

    override fun onRender(deltaTime: Float) {
        artemisWorld.delta = deltaTime
        artemisWorld.process()
    }

    override fun onResize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun onDestroy() {
        Gdx.input.inputProcessor = null
        inputProcessor.clear()
        artemisWorld.dispose()
    }
}