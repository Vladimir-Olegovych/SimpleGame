package org.example.eventbus.event

import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.Connection
import model.Event
import type.EntityType

sealed class BusEvent {
    sealed class ProcessorEvent: BusEvent() {
        data class OnConnected(val connection: Connection) : ProcessorEvent()
        data class OnDisconnected(val connection: Connection) : ProcessorEvent()
        data class OnReceive(val connection: Connection, val event: Event) : ProcessorEvent()
    }

    data class OnReceiveId<T : Event>(val entityId: Int, val event: T) : BusEvent()

    data class CreateClient(val connection: Connection): BusEvent()
    data class RemoveClient(val connection: Connection): BusEvent()
    data class ConnectionToId(val connection: Connection): BusEvent()

    data class CreateEntity(val entityId: Int, val isObserver: Boolean, val entityType: EntityType): BusEvent()
    data class RemoveEntity(val entityId: Int): BusEvent()

    data class CreateBody(val entityId: Int, val vector2: Vector2): BusEvent()
    data class RemoveBody(val entityId: Int): BusEvent()

    companion object {
        const val FIELD_EVENT = "event"
    }
}