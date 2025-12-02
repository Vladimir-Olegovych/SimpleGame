package app.screens.game.screen

import app.di.modules.GameViewport
import app.di.modules.UiViewport
import app.ecs.models.Player
import app.ecs.models.SendEvents
import app.ecs.processors.HotKeysInputProcessor
import app.ecs.processors.LookInputProcessor
import app.ecs.processors.MovementInputProcessor
import app.ecs.processors.ServerInputProcessor
import app.ecs.systems.DrawSystem
import app.ecs.systems.EntitySystem
import app.ecs.systems.ServerSystem
import app.ecs.systems.UiSystem
import app.navigation.Navigation
import com.artemis.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import core.models.settings.ClientPreference
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
    private val onBack: () -> Unit
): Fragment() {

    @Inject
    lateinit var clientPreference: ClientPreference
    @Inject
    lateinit var gameClient: GameClient<GamePacket>
    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var assetManager: AssetManager
    @Inject
    lateinit var spriteBatch: SpriteBatch
    @Inject
    lateinit var camera: OrthographicCamera
    @Inject
    lateinit var stage: Stage
    @Inject
    lateinit var gameViewport: GameViewport
    @Inject
    lateinit var uiViewport: UiViewport

    private lateinit var artemisWorld: World
    private val inputProcessor = CycleInputProcessor()

    override fun onCreate() {
        val entitySystem = EntitySystem()
        val player = Player()
        val sendEvents = SendEvents()

        inputProcessor.addProcessor(stage)
        inputProcessor.addProcessor(MovementInputProcessor(sendEvents))
        inputProcessor.addProcessor(HotKeysInputProcessor(sendEvents))
        inputProcessor.addProcessor(LookInputProcessor(sendEvents))

        artemisWorld = ArtemisWorldBuilder()
            .addSystem(entitySystem)
            .addSystem(DrawSystem())
            .addSystem(UiSystem(onBack))
            .addSystem(ServerSystem())
            .addObject(player)
            .addObject(sendEvents)
            .addObject(dialogManager)
            .addObject(clientPreference)
            .addObject(gameViewport)
            .addObject(uiViewport)
            .addObject(stage)
            .addObject(gameClient)
            .addObject(spriteBatch)
            .addObject(camera)
            .addObject(assetManager)
            .build()

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
        eventBus.process()
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