package app.ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.esotericsoftware.kryonet.Connection
import event.GamePacket
import tools.eventbus.EventBus
import tools.kyro.common.GameNetworkListener

class ServerSystem(
    private val onError: () -> Unit,
    private val onDisconnect: () -> Unit
): GameNetworkListener<GamePacket>, BaseSystem() {

    @Wire
    private lateinit var eventBus: EventBus

    override fun onError(e: Throwable) {
        onError.invoke()
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

    override fun processSystem() {

    }
}