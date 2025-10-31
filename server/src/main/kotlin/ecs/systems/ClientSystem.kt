package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import ecs.components.Client
import event.Event
import event.GamePacket
import event.SendContainer
import kotlinx.coroutines.*
import models.SendType
import org.example.core.models.ServerPreference
import org.example.ecs.event.SystemEvent

@All(Client::class)
class ClientSystem(): IteratingSystem() {

    @Wire private lateinit var serverPreference: ServerPreference

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var clientMapper: ComponentMapper<Client>
    private val tasks = ArrayList<Deferred<Any?>>()

    fun removeClient(systemEvent: SystemEvent.RemoveClient){
        val client = clientMapper[systemEvent.entityId]
        client.dispose()
        clientMapper.remove(systemEvent.entityId)
    }

    fun createClient(systemEvent: SystemEvent.CreateClient) {
        val client = clientMapper.create(systemEvent.entityId)
        client.connection = systemEvent.connection

        client.addEvent(
            Event.CurrentChunkParams(
                chunkRadius = serverPreference.chunkRadius,
                chunkSize = serverPreference.chunkSize
            )
        )
        client.addEvent(
            Event.CurrentPlayer(
                entityId = systemEvent.entityId
            )
        )
    }

    override fun initialize() {}

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

        if(tcpArray.isNotEmpty())
            client.sendPaket(SendContainer(GamePacket(tcpArray), SendType.TCP))
        if(udpArray.isNotEmpty())
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

