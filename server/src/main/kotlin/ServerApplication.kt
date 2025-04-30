package org.example

import model.Event
import org.example.constants.WorldComponents
import org.example.ecs.features.ForceFeature
import org.example.ecs.features.PlayerFeature
import org.example.ecs.features.SendFeature
import org.example.ecs.systems.ClientInputSystem
import org.example.ecs.systems.PhysicSystem
import tools.artemis.world.ArtemisWorldBuilder
import tools.graphics.render.LifecycleUpdater
import tools.kyro.server.GameServer

class ServerApplication(private val port: Int = 5000): LifecycleUpdater() {

    private val artemisWorld = ArtemisWorldBuilder()
        .addSystem(ClientInputSystem())
        .addSystem(PhysicSystem())
        .build()
    private val gameServer = GameServer<Event>(lifecycleScope)

    override fun create() {
        val features = arrayOf(ForceFeature, PlayerFeature, SendFeature)
        features.forEach { artemisWorld.inject(it) }

        gameServer.subscribe<Event>(
            onConnected = { listener, connection ->
                PlayerFeature.createPlayer(connection)
            },
            onDisconnected = { listener, connection ->
                PlayerFeature.removePlayer(connection)
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
                kryo.register(Event::class.java)
                kryo.register(Event.Entity::class.java)
                kryo.register(Event.Player::class.java)
                kryo.register(Event.PlayerVelocity::class.java)
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