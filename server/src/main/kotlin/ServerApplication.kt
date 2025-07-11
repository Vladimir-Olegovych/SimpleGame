package org.example

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import model.Event
import org.example.ecs.systems.*
import org.example.eventbus.ServerEventBus
import org.example.models.ServerPreference
import org.example.models.eventbus.BusEvent
import org.example.values.GameValues
import tools.artemis.world.ArtemisWorldBuilder
import tools.graphics.render.LifecycleUpdater
import tools.kyro.server.GameServer
import tools.preference.JsonPreference
import type.EntityType
import utils.registerAllEvents
import java.util.concurrent.Executor

class ServerApplication(
    private val port: Int = 5000
): LifecycleUpdater(), Executor {

    private val jsonPreference = JsonPreference("server", ServerPreference())

    init { GameValues.setServerPreference(jsonPreference.getPreference()) }

    private val serverEventBus = ServerEventBus()
    private val gameServer = GameServer<Event>()

    private val box2dWold = World(Vector2(0F, 0F), false)

    private val clientSystem = ClientSystem(lifecycleScope)
    private val eventSystem = EventSystem()
    private val chunkSystem = ChunkSystem()
    private val physicsSystem = PhysicsSystem()
    private val moveSystem = MoveSystem()
    private val entitySystem = EntitySystem()

    private val artemisWorld = ArtemisWorldBuilder()
        .addSystem(entitySystem)
        .addSystem(moveSystem)
        .addSystem(physicsSystem)
        .addSystem(chunkSystem)
        .addSystem(eventSystem)
        .addSystem(clientSystem)

        .addObject(box2dWold)
        .addObject(serverEventBus)
        .build()

    override fun create() {
        gameServer.subscribe(serverEventBus.getListener())
        gameServer.start(
            port = port,
            custom = { kryo ->
                kryo.registerAllEvents()
            }
        )

        serverEventBus.addHandler(entitySystem)
        serverEventBus.addHandler(moveSystem)
        serverEventBus.addHandler(physicsSystem)
        serverEventBus.addHandler(chunkSystem)
        serverEventBus.addHandler(eventSystem)
        serverEventBus.addHandler(clientSystem)

        for (i in 0 .. 100) {
            val entityId = artemisWorld.create()

            serverEventBus.sendEvent(BusEvent.CreateEntity(
                entityId, false, EntityType.ENEMY
            ))
            serverEventBus.sendEvent(BusEvent.CreateBody(
                2F, 2F, entityId
            ))
        }
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
        gameServer.stop()
        artemisWorld.dispose()
    }
}