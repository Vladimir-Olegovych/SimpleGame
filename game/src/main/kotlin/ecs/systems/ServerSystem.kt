package ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import ecs.features.EntityInputFeature
import model.Event
import tools.kyro.client.GameClient

class ServerSystem(private val onDisconnected: () -> Unit): BaseSystem() {

    @Wire private lateinit var gameClient: GameClient<Event>

    override fun initialize() {
        EntityInputFeature.initialize(world)
        //Event
        gameClient.subscribe<Event>(
            onDisconnected = { _, _ ->
                gameClient.stop()
                onDisconnected.invoke()
            }
        )
        //Entity
        gameClient.subscribe(
            onReceive = { _, _, data ->
                EntityInputFeature.onReceiveEntity(data)
            }
        )
        //Position
        gameClient.subscribe(
            onReceive = { _, _, data ->
                EntityInputFeature.onReceivePosition(data)
            }
        )
        //Size
        gameClient.subscribe(
            onReceive = { _, _, data ->
                EntityInputFeature.onReceiveSizes(data)
            }
        )
        //Remove
        gameClient.subscribe(
            onReceive = { _, _, data ->
                EntityInputFeature.onReceiveRemove(data)
            }
        )
        //CurrentPLayer
        gameClient.subscribe(
            onReceive = { listener, _, data ->
                EntityInputFeature.onReceiveCurrentPlayer(data)
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