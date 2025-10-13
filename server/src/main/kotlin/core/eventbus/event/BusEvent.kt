package org.example.core.eventbus.event

import alexey.tools.common.collections.IntCollection
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

    data class CreateEntity(val entityId: Int,
                            val entityType: EntityType,
                            val isObserver: Boolean,
                            val isStatic: Boolean,
                            val isPhysical: Boolean,
                            val position: Vector2? = null): BusEvent()

    data class RemoveEntity(val entityId: Int): BusEvent()

    data class CreateBody(val entityId: Int, val vector2: Vector2): BusEvent()
    data class RemoveBody(val entityId: Int): BusEvent()
    data class PauseBody(val entityId: Int): BusEvent()
    data class ResumeBody(val entityId: Int): BusEvent()

    data class ApplyEntityToChunk(val entityId: Int, val vector2: Vector2): BusEvent()
    data class RemoveEntityChunk(val entityId: Int): BusEvent()

    data class ShowEntities(val entityId: Int, val entities: IntCollection): BusEvent()
    data class HideEntities(val entityId: Int, val entities: IntCollection): BusEvent()

    companion object {
        const val FIELD_EVENT = "event"
    }
}