package ecs.components

import alexey.tools.common.collections.IntCollection
import com.artemis.Component
import com.badlogic.gdx.utils.Disposable
import com.esotericsoftware.kryonet.Connection
import event.Event
import event.SendContainer
import models.SendType
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class Client: Disposable, Component() {
    private val events = ConcurrentLinkedQueue<SendContainer<Event>>()
    private var entities = ConcurrentLinkedQueue<Int>()
    var connection: Connection? = null

    fun getEvents(): Queue<SendContainer<Event>> = events
    fun clearEvents() { events.clear() }
    fun addEvent(event: Event, sendType: SendType = SendType.TCP){
        events.add(
            SendContainer(
                data = event,
                sendType = sendType
            )
        )
    }

    fun getEntities(): Iterable<Int> = entities
    fun addEntities(entities: IntCollection) { this.entities.addAll(entities) }
    fun removeEntities(entities: IntCollection) { this.entities.removeAll(entities) }

    override fun dispose() {
        connection = null
        events.clear()
    }

}