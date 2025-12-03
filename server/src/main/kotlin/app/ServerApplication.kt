package app

import alexey.tools.server.level.AdvancedChunkManager
import app.di.appModule
import app.di.itemModule
import app.di.physicsModule
import app.di.systemModule
import app.listeners.contact.ItemContactListener
import com.artemis.World
import core.physics.contact.ContactManager
import event.GamePacket
import kotlinx.coroutines.Dispatchers
import org.example.app.ecs.systems.ChunkSystem
import org.example.app.ecs.systems.ClientSystem
import org.example.app.ecs.systems.EntitySystem
import org.example.app.ecs.systems.EventSystem
import org.example.core.models.settings.ServerPreference
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import tools.eventbus.EventBus
import tools.graphics.render.LifecycleUpdater
import tools.kyro.server.GameServer
import utils.registerAllEvents

class ServerApplication(
    private val port: Int = 5000
): KoinComponent, LifecycleUpdater(1F / 30F, Dispatchers.IO) {

    private val artemisWorld: World by inject()

    private val eventBus: EventBus by inject()
    private val serverPreference: ServerPreference by inject()
    private val chunkManager: AdvancedChunkManager by inject()
    private val contactManager: ContactManager by inject()
    private val gameServer: GameServer<GamePacket> by inject()

    private val clientSystem: ClientSystem by inject()
    private val entitySystem: EntitySystem by inject()
    private val chunkSystem: ChunkSystem by inject()
    private val eventSystem: EventSystem by inject()
    private val itemContactListener: ItemContactListener by inject()

    override fun create() {
        startKoin {
            modules(systemModule, itemModule, physicsModule, appModule)
        }

        gameServer.subscribe(clientSystem)
        eventBus.registerHandler(entitySystem)

        chunkManager.putListener(chunkSystem)
        chunkManager.putListener(eventSystem)
        contactManager.addListener(itemContactListener)

        println("---------------------")
        println(serverPreference.toString())
        println("---------------------")

        gameServer.start(
            port = port,
            bufferSize = 262144,
            custom = { kryo ->
                kryo.registerAllEvents()
            }
        )
    }

    override fun update(deltaTime: Float) {
        try {
            eventBus.process()
            artemisWorld.delta = deltaTime
            artemisWorld.process()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun dispose() {
        gameServer.stop()
        artemisWorld.dispose()
    }
}