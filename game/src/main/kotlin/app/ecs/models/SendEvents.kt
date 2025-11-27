package app.ecs.models

import event.Event
import event.SendContainer
import models.enums.SendType
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class SendEvents {

    private val events = ConcurrentLinkedQueue<SendContainer<Event>>()
    private val delayedEvents = mutableMapOf<Class<*>, Long>()

    fun getEvents(): Queue<SendContainer<Event>> = events
    fun clearEvents() { events.clear() }

    fun addDelayedEvent(delay: Long, event: Event, sendType: SendType = SendType.TCP) {
        val now = System.currentTimeMillis()
        val lastTime = delayedEvents[event::class.java] ?: 0L

        if (now - lastTime >= delay) {
            addEvent(event, sendType)
            delayedEvents[event::class.java] = now
        }
    }

    fun addEvent(event: Event, sendType: SendType = SendType.TCP){
        events.add(
            SendContainer(
                data = event,
                sendType = sendType
            )
        )
    }
}