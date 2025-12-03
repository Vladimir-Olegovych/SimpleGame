package org.example.app.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.ClientComponent
import event.GamePacket
import event.SendContainer
import kotlinx.coroutines.*
import models.network.SendType

@All(ClientComponent::class)
class SendSystem(): IteratingSystem() {

    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private val scope = CoroutineScope(Dispatchers.IO)
    private val sendTasks = ArrayList<Deferred<Any?>>()

    override fun begin() {
        runBlocking {
            sendTasks.forEach { it.await() }
            sendTasks.clear()
        }
    }

    override fun process(entityId: Int) {
        val client = clientComponentMapper[entityId]?: return

        val events = client.getEvents()

        val tcpArray = events.filter { it.sendType == SendType.TCP }.map { it.data }.toTypedArray()
        val udpArray = events.filter { it.sendType == SendType.UDP }.map { it.data }.toTypedArray()

        if(tcpArray.isNotEmpty())
            client.sendPaket(SendContainer(GamePacket(tcpArray), SendType.TCP))
        if(udpArray.isNotEmpty())
            client.sendPaket(SendContainer(GamePacket(udpArray), SendType.UDP))

        client.clearEvents()
    }

    private fun ClientComponent.sendPaket(paket: SendContainer<GamePacket>){
        val deferred = scope.async {
            try {
                when(paket.sendType) {
                    SendType.TCP -> this@sendPaket.connection?.sendTCP(paket.data)
                    SendType.UDP -> this@sendPaket.connection?.sendUDP(paket.data)
                }
            } catch (_: Throwable){

            }
        }
        sendTasks.add(deferred)
    }

    override fun dispose() {
        scope.cancel()
    }
}

