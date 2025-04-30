package ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import ecs.features.EntityInputFeature
import model.Event
import tools.kyro.client.GameClient
import tools.kyro.common.GameNetworkListener

class ServerInputSystem(private val onDisconnected: () -> Unit): BaseSystem() {

    @Wire private lateinit var gameClient: GameClient<Event>
    private val listeners = ArrayList<GameNetworkListener<*>>()

    override fun initialize() {
        EntityInputFeature.initialize(world)
        listeners.add(
            gameClient.subscribe<Event>(
                onDisconnected = { _, _ ->
                    onDisconnected.invoke()
                }
            )
        )
        listeners.add(
            gameClient.subscribe(
                onReceive = { _, _, data -> EntityInputFeature.onReceiveEntity(data) }
            )
        )
        listeners.add(
            gameClient.subscribe(
                onReceive = { _, _, data ->
                    EntityInputFeature.onReceivePlayerDisconnected(data)
                }
            )
        )
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

    @Suppress("UNCHECKED_CAST")
    override fun dispose() {
        listeners.forEach { gameClient.unSubscribe(it as GameNetworkListener<Event>) }
    }
}