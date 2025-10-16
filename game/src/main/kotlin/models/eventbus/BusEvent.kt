package models.eventbus

import com.esotericsoftware.kryonet.Connection
import event.GamePacket

sealed class BusEvent {
    sealed class ProcessorEvent: BusEvent() {
        data class OnConnected(val connection: Connection) : BusEvent()
        data class OnDisconnected(val connection: Connection) : BusEvent()
        data class OnGamePaket(val connection: Connection, val paket: GamePacket) : BusEvent()
    }
}