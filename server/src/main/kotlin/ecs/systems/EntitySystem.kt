package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.ServerPreference
import org.example.ecs.components.*

@All(EntityModel::class)
class EntitySystem: IteratingSystem() {

    @Wire private lateinit var serverPreference: ServerPreference
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var staticPositionMapper: ComponentMapper<StaticPosition>
    private lateinit var physicsMapper: ComponentMapper<Physics>
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var moveMapper: ComponentMapper<Move>

    fun createEntity(busEvent: BusEvent.CreateEntity){
        val entity = entityMapper.create(busEvent.entityId)
        entity.isObserver = busEvent.isObserver
        entity.isPhysical = busEvent.isPhysical
        entity.isStatic = busEvent.isStatic
        entity.textureType = busEvent.textureType
        entity.entityType = busEvent.entityType

        val size = sizeMapper.create(busEvent.entityId)
        size.radius = serverPreference.blockSize / 2F
        size.halfWidth = serverPreference.blockSize / 2F
        size.halfHeight = serverPreference.blockSize / 2F

        if(entity.isPhysical) {
            val move = moveMapper.create(busEvent.entityId)
            physicsMapper.create(busEvent.entityId)
        }

        if (entity.isStatic){
            val staticPosition = staticPositionMapper.create(busEvent.entityId)
            staticPosition.position = busEvent.position
        }
    }

    fun removeEntity(busEvent: BusEvent.RemoveEntity) {
        entityMapper.remove(busEvent.entityId)
        staticPositionMapper.remove(busEvent.entityId)
        physicsMapper.remove(busEvent.entityId)
        sizeMapper.remove(busEvent.entityId)
        moveMapper.remove(busEvent.entityId)
    }


    override fun process(entityId: Int) {

    }
}