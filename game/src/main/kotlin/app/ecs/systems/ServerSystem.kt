package app.ecs.systems

import app.ecs.models.SendEvents
import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import event.GamePacket
import event.SendContainer
import models.enums.SendType
import tools.kyro.client.GameClient

class ServerSystem: BaseSystem() {

    @Wire
    private lateinit var sendEvents: SendEvents
    @Wire
    private  lateinit var gameClient: GameClient<GamePacket>

    override fun processSystem() {
        val events = sendEvents.getEvents().apply { if (isEmpty()) return@processSystem }

        val tcpArray = events.filter { it.sendType == SendType.TCP }.map { it.data }.toTypedArray()
        val udpArray = events.filter { it.sendType == SendType.UDP }.map { it.data }.toTypedArray()

        if(tcpArray.isNotEmpty())
            sendPaket(SendContainer(GamePacket(tcpArray), SendType.TCP))
        if(udpArray.isNotEmpty())
            sendPaket(SendContainer(GamePacket(udpArray), SendType.UDP))

        sendEvents.clearEvents()
    }

    private fun sendPaket(paket: SendContainer<GamePacket>){
        when(paket.sendType) {
            SendType.TCP -> gameClient.sendTCP(paket.data)
            SendType.UDP -> gameClient.sendUDP(paket.data)
        }
    }
}