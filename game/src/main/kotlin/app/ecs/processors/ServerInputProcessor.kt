package app.ecs.processors

import com.esotericsoftware.kryonet.Connection
import event.GamePacket
import tools.eventbus.EventBus
import tools.kyro.common.GameNetworkListener

class ServerInputProcessor(
    private val eventBus: EventBus,
    private val onDisconnect: () -> Unit
): GameNetworkListener<GamePacket> {

    override fun onError(e: Throwable) {
        onDisconnect.invoke()
    }

    override fun onConnected(connection: Connection) {

    }

    override fun onDisconnected(connection: Connection) {
        onDisconnect.invoke()
    }

    override fun onReceive(connection: Connection, value: GamePacket) {
        val events = value.events
        for(event in events) {
            eventBus.sendEvent(event)
        }
    }

}