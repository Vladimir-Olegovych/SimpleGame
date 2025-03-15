package ecs.components

import com.artemis.Component
import com.badlogic.gdx.utils.IntMap
import com.esotericsoftware.kryonet.Connection
import org.example.tools.connection.models.EntityEvent
import org.example.tools.connection.models.Event
import types.EventType
import java.util.concurrent.ConcurrentLinkedQueue

class Client: Component() {
    private val events = ConcurrentLinkedQueue<Event>()
    private val entityEvents = ConcurrentLinkedQueue<EntityEvent>()
    var connection: Connection? = null
    val connectionWallMap = IntMap<Int>()

    fun getQueue(): ConcurrentLinkedQueue<Event> = events
    fun getEntityQueue(): ConcurrentLinkedQueue<EntityEvent> = entityEvents

    fun addEvent(eventType: EventType, content: Any){
        events.add(Event(eventId = eventType.id, content = content))
    }

    fun addEntityEvent(entityId: Int, eventType: EventType, content: Any){
        entityEvents.forEach { if(it.entityId == entityId) return@addEntityEvent }
        entityEvents.add(EntityEvent(entityId = entityId, eventId = eventType.id, content = content))
    }

}