package org.example

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import model.Event
import org.example.ecs.features.ForceFeature
import org.example.ecs.features.PlayerFeature
import org.example.ecs.features.WorldFeature
import org.example.ecs.systems.ClientInputSystem
import org.example.ecs.systems.EntitySystem
import tools.artemis.world.ArtemisWorldBuilder
import tools.graphics.render.LifecycleUpdater
import tools.kyro.server.GameServer
import utils.registerAllEvents

class ServerApplication(private val port: Int = 5000): LifecycleUpdater() {

    private val gameServer = GameServer<Event>(lifecycleScope)

    private val artemisWorld = ArtemisWorldBuilder()
        .addSystem(ClientInputSystem())
        .addSystem(EntitySystem())
        .addObject(World(Vector2(0F, 0F), false))
        .build()

    override fun create() {
        gameServer.subscribe<Event>(
            onConnected = { listener, connection ->
                val playerId = artemisWorld.create()
                WorldFeature.createPlayer(playerId)
                PlayerFeature.createPlayer(playerId, connection)
            },
            onDisconnected = { listener, connection ->
                val playerId = PlayerFeature.getPlayers()[connection]?: return@subscribe
                WorldFeature.removePlayer(playerId)
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