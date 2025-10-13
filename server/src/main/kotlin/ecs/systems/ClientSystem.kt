package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.esotericsoftware.kryonet.Connection
import ecs.components.Client
import kotlinx.coroutines.*
import model.Event
import model.GamePaket
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.ServerPreference
import org.example.ecs.processors.ClientProcessor
import tools.eventbus.annotation.EventCallback

@All(Client::class)
class ClientSystem(): IteratingSystem() {

    @Wire private lateinit var serverPreference: ServerPreference
    @Wire private lateinit var clientProcessor: ClientProcessor

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var clientMapper: ComponentMapper<Client>
    private val playersMap = HashMap<Connection, Int>()
    private val tasks = ArrayList<Deferred<Unit>>()

    @EventCallback
    fun removeClient(busEvent: BusEvent.RemoveClient){
        val entityId = playersMap[busEvent.connection]?: return
        val client = clientMapper[entityId]
        client.dispose()
        clientMapper.remove(entityId)
        playersMap.remove(busEvent.connection)
    }

    @EventCallback
    fun createClient(busEvent: BusEvent.CreateClient) {
        val entityId = world.create()
        playersMap[busEvent.connection] = entityId
        val client = clientMapper.create(entityId)
        client.connection = busEvent.connection

        client.addEvent(
            Event.CurrentChunkParams(
                chunkRadius = serverPreference.chunkRadius,
                chunkSize = serverPreference.chunkSize
            )
        )
        client.addEvent(
            Event.CurrentPlayer(
                entityId = entityId
            )
        )
    }

    @EventCallback
    fun connectionToId(busEvent: BusEvent.ConnectionToId): Int? {
        return playersMap[busEvent.connection]
    }

    override fun initialize() {
        clientProcessor.create(world)
    }

    override fun begin() {
        runBlocking {
            tasks.forEach { it.await() }
            tasks.clear()
        }
    }

    override fun process(entityId: Int) {
        val client = clientMapper[entityId]?: return
        val events = client.getEvents()
        val gamePaket = GamePaket(events.toTypedArray())
        tasks.add(
            scope.async { client.sendPaket(gamePaket) }
        )
        client.clearEvents()
    }

    private fun Client.sendPaket(gamePaket: GamePaket){
        if (gamePaket.events.isEmpty()) return
        try {
            this.connection?.sendTCP(gamePaket)
        } catch (e: Throwable){
            e.printStackTrace()
        }
    }
}

