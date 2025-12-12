package app.ecs.systems.event

import app.event.ChunkEvent
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import ecs.components.ClientComponent
import event.Event
import org.example.app.ecs.components.EntityTypeComponent
import tools.eventbus.annotation.BusEvent

class EntityTypeEventSystem: BaseSystem() {

    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private lateinit var entityTypeComponentMapper: ComponentMapper<EntityTypeComponent>

    @BusEvent
    fun showEntities(event: ChunkEvent.Show){
        for (activatorId in event.activators){
            val clientComponent = clientComponentMapper[activatorId]?: continue
            for (entityId in event.entities) {
                val entityTypeComponent = entityTypeComponentMapper[entityId]?: continue
                clientComponent.addEvent(Event.EntityTypeEvent(
                    entityId = entityId,
                    entityType = entityTypeComponent.entityType
                ))
            }
        }
    }

    override fun processSystem() {}

}