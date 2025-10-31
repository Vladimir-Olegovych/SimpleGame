package ecs.processors

import com.esotericsoftware.kryonet.Connection
import event.GamePacket
import tools.artemis.processor.GameProcessor
import tools.eventbus.EventBus
import tools.kyro.common.GameNetworkListener

class ClientProcessor(
    private val eventBus: EventBus,
    private val onDisconnect: () -> Unit
): GameProcessor, GameNetworkListener<GamePacket> {

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