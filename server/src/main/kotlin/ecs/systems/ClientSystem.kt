package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.esotericsoftware.kryonet.Connection
import ecs.components.Client
import event.Event
import event.GamePacket
import event.SendContainer
import kotlinx.coroutines.*
import models.SendType
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.ServerPreference
import org.example.ecs.processors.ClientProcessor

@All(Client::class)
class ClientSystem(): IteratingSystem() {

    @Wire private lateinit var serverPreference: ServerPreference
    @Wire private lateinit var clientProcessor: ClientProcessor

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var clientMapper: ComponentMapper<Client>
    private val playersMap = HashMap<Connection, Int>()
    private val tasks = ArrayList<Deferred<Any?>>()

    fun removeClient(busEvent: BusEvent.RemoveClient){
        val entityId = playersMap[busEvent.connection]?: return
        val client = clientMapper[entityId]
        client.dispose()
        clientMapper.remove(entityId)
        playersMap.remove(busEvent.connection)
    }

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

    fun connectionToId(connection: Connection): Int? {
        return playersMap[connection]
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

        val tcpArray = events.filter { it.sendType == SendType.TCP }.map { it.data }.toTypedArray()
        val udpArray = events.filter { it.sendType == SendType.UDP }.map { it.data }.toTypedArray()

        client.sendPaket(SendContainer(GamePacket(tcpArray), SendType.TCP))
        client.sendPaket(SendContainer(GamePacket(udpArray), SendType.UDP))

        client.clearEvents()
    }

    private fun Client.sendPaket(paket: SendContainer<GamePacket>){
        val deferred = scope.async {
            try {
                when(paket.sendType) {
                    SendType.TCP -> this@sendPaket.connection?.sendTCP(paket.data)
                    SendType.UDP -> this@sendPaket.connection?.sendUDP(paket.data)
                }
            } catch (e: Throwable){
                e.printStackTrace()
            }
        }
        tasks.add(deferred)
    }
}

