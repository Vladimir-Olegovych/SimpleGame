package ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import ecs.features.EntityInputFeature
import model.Event
import tools.kyro.client.GameClient

class ServerInputSystem(private val onDisconnected: () -> Unit): BaseSystem() {

    @Wire private lateinit var gameClient: GameClient<Event>

    override fun initialize() {
        EntityInputFeature.initialize(world)
        //Event
        gameClient.subscribe<Event>(
            onDisconnected = { _, _ ->
                onDisconnected.invoke()
            }
        )
        //OnDisconnectPlayer
        gameClient.subscribe(
            onReceive = { _, _, data ->
                EntityInputFeature.onReceivePlayerDisconnected(data)
            }
        )
        //Wall
        gameClient.subscribe(
            onReceive = { _, _, data ->
                EntityInputFeature.onReceiveWall(data)
            }
        )
        //Enemy
        gameClient.subscribe(
            onReceive = { _, _, data ->
                EntityInputFeature.onReceiveEnemy(data)
            }
        )
        //Player
        gameClient.subscribe(
            onReceive = { listener, _, data ->
                EntityInputFeature.onReceivePlayer(data)
                gameClient.unSubscribe(listener)
            }
        )

    }
    override fun processSystem() {
        EntityInputFeature.notify(0)
    }

    override fun dispose() {
        gameClient.unSubscribeAll()
    }
}