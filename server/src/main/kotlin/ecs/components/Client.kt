package ecs.components

import com.artemis.Component
import com.esotericsoftware.kryonet.Connection
import model.Event
import java.util.concurrent.ConcurrentLinkedQueue

class Client: Component() {
    private val events = ConcurrentLinkedQueue<Event>()
    var connection: Connection? = null

    fun getEvents(): Iterable<Event> = events.asIterable()
    fun clearEvents() { events.clear() }

    fun addEvent(event: Event){
        events.add(event)
    }
}