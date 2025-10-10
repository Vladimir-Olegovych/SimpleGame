package ecs.components

import com.artemis.Component
import com.badlogic.gdx.utils.Disposable
import com.esotericsoftware.kryonet.Connection
import model.Event
import tools.chunk.Chunk
import java.util.concurrent.ConcurrentLinkedQueue

class Client: Disposable, Component() {
    private val events = ConcurrentLinkedQueue<Event>()
    private val chunks = ConcurrentLinkedQueue<Chunk>()
    var connection: Connection? = null

    fun getEvents(): Iterable<Event> = events.asIterable()
    fun clearEvents() { events.clear() }
    fun addEvent(event: Event){ events.add(event) }

    fun getChunks(): Iterable<Chunk> = chunks.asIterable()
    fun addChunk(chunk: Chunk){ chunks.add(chunk) }
    fun removeChunk(chunk: Chunk){ chunks.add(chunk) }

    override fun dispose() {
        connection = null
        chunks.clear()
        events.clear()
    }

}