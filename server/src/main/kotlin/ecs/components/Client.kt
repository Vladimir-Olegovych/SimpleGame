package ecs.components

import com.artemis.Component
import com.esotericsoftware.kryonet.Connection
import model.Event
import java.util.concurrent.ConcurrentLinkedQueue

class Client: Component() {
    private val events = ConcurrentLinkedQueue<Event>()
    var connection: Connection? = null
    val wallMap = HashMap<Int, Int>()

    fun getEvents(): Iterable<Event> = events.asIterable()
    fun clearEvents() { events.clear() }

    fun addEvent(event: Event.Entity){
        events.forEach {
            if (it is Event.Entity && it.entityId == event.entityId) return@addEvent
        }
        events.add(event)
    }
}