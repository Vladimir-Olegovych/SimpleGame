package game.screens

import com.artemis.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import ecs.systems.DrawSystem
import ecs.systems.InputSystem
import ecs.systems.ServerInputSystem
import eventbus.GameEventBus
import game.MainGame
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tools.artemis.world.ArtemisWorldBuilder
import tools.input.CycleInputProcessor
import tools.screens.screen.ScreenContext

class GameScreen(private val game: MainGame): ScreenContext(), KoinComponent {

    private val stage by inject<Stage>()
    private val shapeRenderer by inject<ShapeRenderer>()
    private val spriteBatch by inject<SpriteBatch>()
    private val assetManager by inject<AssetManager>()
    private val eventBus: GameEventBus by inject()

    private var world: World? = null

    override fun onAttached() {
        val cycleInputProcessor = CycleInputProcessor()
        val inputSystem = InputSystem()

        val processors = arrayOf(
            inputSystem,
            stage
        )

        val systems = arrayOf(
            DrawSystem(),
            inputSystem,
            ServerInputSystem()
        )

        val objects = arrayOf(
            stage,
            shapeRenderer,
            spriteBatch,
            assetManager,
            eventBus
        )

        cycleInputProcessor.setArrayOfProcessors(processors)

        this.world = ArtemisWorldBuilder.Builder()
            .setRegisteredObjectsArray(objects)
            .setSystemArray(systems)
            .build()

        Gdx.input.inputProcessor = cycleInputProcessor
    }

    override fun onDetached() {
        Gdx.input.inputProcessor = null
        world?.dispose()
    }
    override fun onPause() {
        println("onPause GameScreen")
    }

    override fun onResume() {
        println("onResume GameScreen")
    }

    override fun onResize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun onRender(deltaTime: Float) {
        world?.delta = Gdx.graphics.deltaTime
        world?.process()
    }

    override fun dispose() {
        super.dispose()
        world?.dispose()
    }
}