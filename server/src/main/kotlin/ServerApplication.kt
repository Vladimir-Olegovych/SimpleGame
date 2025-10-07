package org.example

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import model.Event
import org.example.ecs.processors.impl.ChunkProcessor
import org.example.ecs.processors.impl.ClientProcessor
import org.example.ecs.systems.*
import org.example.eventbus.ServerEventBus
import org.example.models.ServerPreference
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
        ClientSystem(lifecycleScope),
        EventSystem(),
        MoveSystem(),
        PhysicsSystem(),
        ChunkSystem(),
        EntitySystem()
    )

    private val processors = arrayOf(
        ClientProcessor(serverEventBus),
        ChunkProcessor(serverEventBus, serverPreferences)
    )

    private val artemisWorld = ArtemisWorldBuilder()
        .addSystems(systems)
        .addObjects(processors)
        .addObject(serverPreferences)
        .build()

    init {
        serverEventBus.addHandlers(processors)
        serverEventBus.addHandlers(systems)
    }

    override fun create() {
        processors.forEach { it.create(artemisWorld) }
        gameServer.subscribe(serverEventBus.getListener())
        gameServer.start(
            port = port,
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