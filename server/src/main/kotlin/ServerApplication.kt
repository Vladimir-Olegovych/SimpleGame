package org.example

import model.Event
import org.example.ecs.features.ForceFeature
import org.example.ecs.features.PlayerFeature
import org.example.ecs.systems.ClientInputSystem
import org.example.ecs.systems.PhysicSystem
import tools.artemis.world.ArtemisWorldBuilder
import tools.graphics.render.LifecycleUpdater
import tools.kyro.server.GameServer
import utils.registerAllEvents

class ServerApplication(private val port: Int = 5000): LifecycleUpdater() {

    private val artemisWorld = ArtemisWorldBuilder()
        .addSystem(ClientInputSystem())
        .addSystem(PhysicSystem())
        .build()
    private val gameServer = GameServer<Event>(lifecycleScope)

    override fun create() {
        gameServer.subscribe<Event>(
            onConnected = { listener, connection ->
                PlayerFeature.createPlayer(connection)
            },
            onDisconnected = { listener, connection ->
                PlayerFeature.removePlayer(connection)
                connection.close()
            },
            onReceive = { listener, connection, data ->
                when(data){
                    is Event.PlayerVelocity -> {
                        val playerId = PlayerFeature.getPlayers()[connection]?: return@subscribe
                        ForceFeature.applyForce(playerId, data.x, data.y)
                    }
                    else -> {}
                }
            }
        )
        gameServer.start(
            port = port,
            custom = { kryo ->
                kryo.registerAllEvents()
            }
        )
    }

    override fun update(deltaTime: Float) {
        artemisWorld.delta = deltaTime
        artemisWorld.process()
    }

    override fun dispose() {
        gameServer.stop()
        artemisWorld.dispose()
    }
}