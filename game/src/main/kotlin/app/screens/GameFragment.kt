package app.screens

import model.Event
import app.navigation.Navigation
import com.artemis.World
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import ecs.components.Player
import ecs.systems.DrawSystem
import ecs.systems.InputSystem
import ecs.systems.ServerInputSystem
import tools.artemis.world.ArtemisWorldBuilder
import tools.graphics.input.CycleInputProcessor
import tools.graphics.screens.fragment.Fragment
import tools.kyro.client.GameClient
import javax.inject.Inject


class GameFragment(
    private val navigation: Navigation.Game,
): Fragment() {

    @Inject lateinit var gameClient: GameClient<Event>
    @Inject lateinit var shapeRenderer: ShapeRenderer
    @Inject lateinit var camera: OrthographicCamera
    @Inject lateinit var viewport: Viewport

    private lateinit var artemisWorld: World
    private val inputProcessor = CycleInputProcessor()


    override fun onCreate(game: Game) {
        val inputSystem = InputSystem()
        val drawSystem = DrawSystem()
        val serverInputSystem = ServerInputSystem()

        inputProcessor.addProcessor(inputSystem)
        artemisWorld = ArtemisWorldBuilder()
            .addSystem(inputSystem)
            .addSystem(drawSystem)
            .addSystem(serverInputSystem)
            .addObject(gameClient)
            .addObject(shapeRenderer)
            .addObject(camera)
            .addObject(Player())
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