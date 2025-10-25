package ecs.processors

import core.models.eventbus.BusEvent
import ecs.systems.EntitySystem
import event.Event
import tools.artemis.processor.GameProcessor
import tools.eventbus.annotation.EventCallback

class ClientProcessor(
    private val entitySystem: EntitySystem,
    private val onDisconnect: () -> Unit
): GameProcessor {

    @EventCallback
    fun onConnected(busEvent: BusEvent.ProcessorEvent.OnConnected){

    }

    @EventCallback
    fun onDisconnected(busEvent: BusEvent.ProcessorEvent.OnDisconnected){
        onDisconnect.invoke()
    }

    @EventCallback
    private fun onGamePaket(busEvent: BusEvent.ProcessorEvent.OnGamePaket){
        val events = busEvent.paket.events
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