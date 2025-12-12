package app.ecs.systems.event

import app.ecs.components.models.ServerTime
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import ecs.components.ClientComponent
import event.Event
import models.network.SendType

@All(ClientComponent::class)
class TimeEventSystem: IteratingSystem() {

    @Wire private lateinit var serverTime: ServerTime
    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>

    override fun process(entityId: Int) {
        val clientComponent = clientComponentMapper[entityId]?: return
        clientComponent.addEvent(
            event = Event.Time(
                time = serverTime.time
            ),
            sendType = SendType.UDP
        )
    }
}