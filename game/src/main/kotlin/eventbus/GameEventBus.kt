package eventbus

import com.esotericsoftware.kryonet.Connection
import model.Event
import model.GamePaket
import models.eventbus.BusEvent
import tools.eventbus.EventBus
import tools.kyro.common.GameNetworkListener

class GameEventBus: EventBus<BusEvent>() {

    private val listener = object : GameNetworkListener<GamePaket>() {
        override fun onConnected(connection: Connection) {
            sendEvent(BusEvent.ProcessorEvent.OnConnected(connection))
        }

        override fun onDisconnected(connection: Connection) {
            sendEvent(BusEvent.ProcessorEvent.OnDisconnected(connection))
            connection.close()
        }

        override fun onReceive(connection: Connection, value: GamePaket) {
            sendEvent(BusEvent.ProcessorEvent.OnGamePaket(connection, value))
        }

    }

    fun getListener(): GameNetworkListener<GamePaket> = listener

}