package app.screens.game.screen

import app.di.GameViewport
import app.di.UiViewport
import app.ecs.models.GlobalAngle
import app.ecs.models.Player
import app.ecs.models.SendEvents
import app.ecs.systems.*
import app.entity.translator.ServerEntityTranslator
import app.event.UiEvent
import app.navigation.Navigation
import app.screens.game.ui.dialog.MenuDialog
import app.screens.game.ui.inventory.InventoryUI
import app.screens.game.ui.menu.MenuUI
import com.artemis.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import core.models.settings.ClientPreference
import core.textures.TextureStorage
import event.GamePacket
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tools.artemis.world.ArtemisWorldBuilder
import tools.eventbus.EventBus
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
    private val inputMultiplexer = InputMultiplexer()
    private val textureStorage = TextureStorage(assetManager)

    override fun onCreate() {
        val uiSystem = UiSystem()
        val inputSystem = InputSystem(onQuit = onBack)
        val serverSystem = ServerSystem(onError = onBack, onDisconnect = onBack)

        val serverEntityTranslator = ServerEntityTranslator()

        artemisWorld = ArtemisWorldBuilder()
            .addSystem(EntitySystem())
            .addSystem(serverSystem)
            .addSystem(DrawSystem())
            .addSystem(LightingSystem())
            .addSystem(CameraSystem())
            .addSystem(uiSystem)
            .addSystem(inputSystem)
            .addSystem(SendSystem())
            .addObject(Player())
            .addObject(GlobalAngle())
            .addObject(SendEvents())
            .addObject(textureStorage)
            .addObject(dialogManager)
            .addObject(clientPreference)
            .addObject(gameViewport)
            .addObject(uiViewport)
            .addObject(stage)
            .addObject(eventBus)
            .addObject(gameClient)
            .addObject(spriteBatch)
            .addObject(camera)
            .addObject(assetManager)
            .addObject(serverEntityTranslator)
            .addObject(MenuDialog())
            .addObject(InventoryUI())
            .addObject(MenuUI())
            .build()

        serverEntityTranslator.setup(artemisWorld)

        inputMultiplexer.addProcessor(stage)
        inputMultiplexer.addProcessor(inputSystem)

        eventBus.registerHandler(serverEntityTranslator)

        for (system in artemisWorld.systems) {
            eventBus.registerHandler(system)
        }

        Gdx.input.inputProcessor = inputMultiplexer
        gameClient.subscribe(serverSystem)

        gameClient.start(
            address = navigation.address,
            port = navigation.port,
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
        eventBus.sendEvent(UiEvent.Resize(width, height))
    }

    override fun onDestroy() {
        Gdx.input.inputProcessor = null
        stage.clear()

        gameClient.stop()
        eventBus.clear()
        gameClient.unSubscribeAll()

        textureStorage.clear()
        inputMultiplexer.clear()
        artemisWorld.dispose()
    }

}