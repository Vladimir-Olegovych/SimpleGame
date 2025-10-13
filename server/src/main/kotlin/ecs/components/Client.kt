package ecs.components

import alexey.tools.common.collections.IntCollection
import com.artemis.Component
import com.badlogic.gdx.utils.Disposable
import com.esotericsoftware.kryonet.Connection
import model.Event
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class Client: Disposable, Component() {
    private val events = ConcurrentLinkedQueue<Event>()
    private var entities = ConcurrentLinkedQueue<Int>()
    var connection: Connection? = null

    fun getEvents(): Queue<Event> = events
    fun clearEvents() { events.clear() }
    fun addEvent(event: Event){ events.add(event) }

    fun getEntities(): Iterable<Int> = entities
    fun addEntities(entities: IntCollection) { this.entities.addAll(entities) }
    fun removeEntities(entities: IntCollection) { this.entities.removeAll(entities) }

    override fun dispose() {
        connection = null
        events.clear()
    }

}