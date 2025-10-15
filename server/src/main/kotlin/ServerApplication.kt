package org.example

import com.artemis.World
import event.Event
import org.example.core.eventbus.ServerEventBus
import org.example.core.models.ServerPreference
import org.example.ecs.processors.ChunkProcessor
import org.example.ecs.processors.ClientProcessor
import org.example.ecs.systems.*
import tools.artemis.world.ArtemisWorldBuilder
import tools.graphics.render.LifecycleUpdater
import tools.kyro.server.GameServer
import tools.preference.JsonPreference
import utils.registerAllEvents
import java.util.concurrent.Executor

class ServerApplication(
    private val port: Int = 5000
): LifecycleUpdater(), Executor {
    private val jsonPreference = JsonPreference("server", ServerPreference())
    private val serverPreferences = jsonPreference.getPreference()

    private val serverEventBus = ServerEventBus()
    private val gameServer = GameServer<Event>()

    private val systems = arrayOf(
        EventSystem(),
        ClientSystem(),
        MoveSystem(),
        PhysicsSystem(),
        ChunkSystem(),
        EntitySystem()
    )

    private val processors = arrayOf(
        ClientProcessor(serverEventBus),
        ChunkProcessor(serverPreferences)
    )

    init {
        serverEventBus.addHandlers(processors)
        serverEventBus.addHandlers(systems)
    }

    private val artemisWorld: World = ArtemisWorldBuilder()
        .addSystems(systems)
        .addObjects(processors)
        .addInjects(processors)
        .addObject(serverPreferences)
        .build()

    override fun create() {
        gameServer.subscribe(serverEventBus.getListener())
        gameServer.start(
            port = port,
            bufferSize = 262144,
            custom = { kryo ->
                kryo.registerAllEvents()
            }
        )
    }

    override fun update(deltaTime: Float) {
        artemisWorld.delta = deltaTime
        try {
            artemisWorld.process()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun dispose() {
        serverEventBus.clearHandlers()
        gameServer.stop()
        artemisWorld.dispose()
    }
}