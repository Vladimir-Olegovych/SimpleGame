package ecs.processors

import com.artemis.World
import eventbus.GameEventBus
import models.eventbus.BusEvent
import tools.artemis.processor.GameProcessor
import tools.eventbus.annotation.EventCallback

class ClientProcessor(
    private val gameEventBus: GameEventBus,
    private val onDisconnect: () -> Unit
): GameProcessor {

    override fun create(artemisWorld: World) {

    }

    @EventCallback
    private fun onDisconnect(busEvent: BusEvent.ProcessorEvent.OnDisconnected){
        onDisconnect.invoke()
    }

    @EventCallback
    private fun onGamePaket(busEvent: BusEvent.ProcessorEvent.OnGamePaket){
        val events = busEvent.paket.events
        events.forEach {
            gameEventBus.sendEvent(BusEvent.OnReceive(busEvent.connection, it))
        }
    }
}