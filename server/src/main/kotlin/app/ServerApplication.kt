package app

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.World
import app.di.components.AppComponent
import app.di.components.DaggerAppComponent
import event.GamePacket
import kotlinx.coroutines.Dispatchers
import org.example.app.ecs.systems.*
import org.example.app.listeners.contact.ItemContactListener
import org.example.core.models.settings.ServerPreference
import org.example.core.physics.contact.ContactManager
import tools.eventbus.EventBus
import tools.graphics.render.LifecycleUpdater
import tools.kyro.server.GameServer
import utils.registerAllEvents
import javax.inject.Inject

class ServerApplication(
    private val port: Int = 5000
): LifecycleUpdater(1F / 30F, Dispatchers.IO) {

    private lateinit var appComponent: AppComponent

    @Inject lateinit var artemisWorld: World

    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var serverPreference: ServerPreference
    @Inject lateinit var chunkManager: AdvancedChunkManager
    @Inject lateinit var contactManager: ContactManager
    @Inject lateinit var gameServer: GameServer<GamePacket>

    @Inject lateinit var clientSystem: ClientSystem
    @Inject lateinit var entitySystem: EntitySystem
    @Inject lateinit var chunkSystem: ChunkSystem
    @Inject lateinit var eventSystem: EventSystem
    @Inject lateinit var itemContactListener: ItemContactListener

    override fun create() {
        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)

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