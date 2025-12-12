package app.ecs.systems.event

import app.event.ChunkEvent
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import ecs.components.ClientComponent
import event.Event
import tools.eventbus.annotation.BusEvent

class EntityEventSystem: BaseSystem() {

    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>

    @BusEvent
    fun showEntities(event: ChunkEvent.Show){
        for (activatorId in event.activators){
            val clientComponent = clientComponentMapper[activatorId]?: continue
            clientComponent.addEntities(event.entities)
            for (entityId in event.entities) {
                clientComponent.addEvent(Event.Entity(entityId = entityId))
            }
        }
    }

    @BusEvent
    fun hideEntities(event: ChunkEvent.Hide){
        for (activatorId in event.activators){
            val clientComponent = clientComponentMapper[activatorId]?: continue
            clientComponent.removeEntities(event.entities)
            for (entityId in event.entities) {
                clientComponent.addEvent(Event.Remove(entityId = entityId))
            }
        }
    }

    override fun processSystem() {}

}