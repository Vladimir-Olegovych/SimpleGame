package ecs.processors

import com.esotericsoftware.kryonet.Connection
import ecs.systems.EntitySystem
import event.Event
import event.GamePacket
import tools.artemis.processor.GameProcessor
import tools.kyro.common.GameNetworkListener

class ClientProcessor(
    private val entitySystem: EntitySystem,
    private val onDisconnect: () -> Unit
): GameProcessor, GameNetworkListener<GamePacket> {

    override fun onConnected(connection: Connection) {

    }

    override fun onDisconnected(connection: Connection) {
        onDisconnect.invoke()
    }

    override fun onReceive(connection: Connection, value: GamePacket) {
        val events = value.events
        for(event in events) {
            when (event){
                is Event.Entity -> entitySystem.setEntity(event = event)
                is Event.Position -> entitySystem.setPosition(event = event)
                is Event.Size -> entitySystem.setSize(event = event)
                is Event.Angle -> entitySystem.setAngle(event = event)
                is Event.CurrentChunkParams -> entitySystem.setChunkParams(event = event)
                is Event.Remove -> entitySystem.setRemove(event = event)
                is Event.CurrentPlayer -> entitySystem.setCurrentPlayer(event = event)
                else -> {}
            }
        }
    }

}