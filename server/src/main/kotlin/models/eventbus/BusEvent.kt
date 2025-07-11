package org.example.models.eventbus

import com.esotericsoftware.kryonet.Connection
import model.Event
import type.EntityType

sealed class BusEvent {
    data class OnConnected(val connection: Connection): BusEvent()
    data class OnDisconnected(val connection: Connection): BusEvent()
    data class OnReceive(val connection: Connection, val event: Event): BusEvent()

    data class CreateEntity(val entityId: Int, val isObserver: Boolean, val entityType: EntityType): BusEvent()
    data class RemoveEntity(val entityId: Int): BusEvent()

    data class CreateBody(val x: Float, val y: Float, val entityId: Int): BusEvent()
    data class RemoveBody(val entityId: Int): BusEvent()

    data class OnReceiveId<T: Event>(val entityId: Int, val event: T): BusEvent()

    companion object {
        const val FIELD_EVENT = "event"
    }
}