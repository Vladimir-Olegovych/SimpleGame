package app.ecs.systems.event

import app.event.ChunkEvent
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import ecs.components.ClientComponent
import event.Event
import org.example.app.ecs.components.SizeComponent
import tools.eventbus.annotation.BusEvent

class SizeEventSystem: BaseSystem() {

    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>

    @BusEvent
    fun showEntities(event: ChunkEvent.Show){
        for (activatorId in event.activators){
            val clientComponent = clientComponentMapper[activatorId]?: continue
            for (entityId in event.entities) {
                val sizeComponent = sizeComponentMapper[entityId]?: continue
                clientComponent.addEvent(
                    Event.Size(
                        entityId = entityId,
                        radius = sizeComponent.radius,
                        height = sizeComponent.height,
                        width = sizeComponent.width
                    )
                )
            }
        }
    }

    override fun processSystem() {}

}