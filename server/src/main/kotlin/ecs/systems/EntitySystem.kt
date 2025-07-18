package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Move
import org.example.ecs.components.Size
import org.example.eventbus.event.BusEvent
import tools.eventbus.annotation.EventCallback

@All(EntityModel::class)
class EntitySystem: IteratingSystem() {

    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var moveMapper: ComponentMapper<Move>

    @EventCallback
    private fun createEntity(busEvent: BusEvent.CreateEntity){
        val entity = entityMapper.create(busEvent.entityId)
        entity.isObserver = busEvent.isObserver
        entity.entityType = busEvent.entityType
        val size = sizeMapper.create(busEvent.entityId)
        size.radius = 0.1F
        val move = moveMapper.create(busEvent.entityId)
    }

    @EventCallback
    private fun removeEntity(busEvent: BusEvent.RemoveEntity) {
        entityMapper.remove(busEvent.entityId)
        sizeMapper.remove(busEvent.entityId)
    }

    override fun process(entityId: Int) {

    }
}