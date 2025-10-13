package models.eventbus

import com.esotericsoftware.kryonet.Connection
import model.Event
import model.GamePaket

sealed class BusEvent {
    sealed class ProcessorEvent: BusEvent() {
        data class OnConnected(val connection: Connection) : BusEvent()
        data class OnDisconnected(val connection: Connection) : BusEvent()
        data class OnGamePaket(val connection: Connection, val paket: GamePaket) : BusEvent()
    }

    data class OnReceive<T: Event>(val connection: Connection, val event: T): BusEvent()

    companion object {
        const val FIELD_EVENT = "event"
    }
}