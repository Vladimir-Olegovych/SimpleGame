package org.example.eventbus.event

import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.Connection
import model.Event
import type.EntityType

sealed class BusEvent {
    data class OnConnected(val connection: Connection): BusEvent()
    data class OnDisconnected(val connection: Connection): BusEvent()
    data class OnReceive(val connection: Connection, val event: Event): BusEvent()
    data class OnReceiveId<T: Event>(val entityId: Int, val event: T): BusEvent()

    data class ApplyEntityToChunk(val entityId: Int, val vector2: Vector2): BusEvent()
    data class RemoveEntityChunk(val entityId: Int): BusEvent()

    data class CreateEntity(val entityId: Int, val isObserver: Boolean, val entityType: EntityType): BusEvent()
    data class RemoveEntity(val entityId: Int): BusEvent()

    data class CreateBody(val entityId: Int, val vector2: Vector2): BusEvent()
    data class RemoveBody(val entityId: Int): BusEvent()

    companion object {
        const val FIELD_EVENT = "event"
    }
}