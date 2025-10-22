package core.eventbus

import com.esotericsoftware.kryonet.Connection
import event.GamePacket
import core.models.eventbus.BusEvent
import tools.eventbus.EventBus
import tools.kyro.common.GameNetworkListener

class GameEventBus: EventBus<BusEvent>() {

    private val listener = object : GameNetworkListener<GamePacket>() {
        override fun onConnected(connection: Connection) {
            sendEvent(BusEvent.ProcessorEvent.OnConnected(connection))
        }

        override fun onDisconnected(connection: Connection) {
            sendEvent(BusEvent.ProcessorEvent.OnDisconnected(connection))
            connection.close()
        }

        override fun onReceive(connection: Connection, value: GamePacket) {
            sendEvent(BusEvent.ProcessorEvent.OnGamePaket(connection, value))
        }

    }

    fun getListener(): GameNetworkListener<GamePacket> = listener

}