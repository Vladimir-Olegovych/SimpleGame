package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import org.example.core.models.ServerPreference
import org.example.ecs.components.*
import org.example.ecs.event.SystemEvent

@All(EntityModel::class)
class EntitySystem: IteratingSystem() {

    @Wire private lateinit var serverPreference: ServerPreference
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var staticPositionMapper: ComponentMapper<StaticPosition>
    private lateinit var physicsMapper: ComponentMapper<Physics>
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var moveMapper: ComponentMapper<Move>

    fun createEntity(systemEvent: SystemEvent.CreateEntity){
        val entity = entityMapper.create(systemEvent.entityId)
        entity.isObserver = systemEvent.isObserver
        entity.isPhysical = systemEvent.isPhysical
        entity.isStatic = systemEvent.isStatic
        entity.textureType = systemEvent.textureType
        entity.entityType = systemEvent.entityType

        val size = sizeMapper.create(systemEvent.entityId)
        val halfSize = serverPreference.blockSize / 2F
        size.radius = halfSize
        size.halfWidth = halfSize
        size.halfHeight = halfSize

        if(entity.isPhysical) {
            val move = moveMapper.create(systemEvent.entityId)
            physicsMapper.create(systemEvent.entityId)
        }

        if (entity.isStatic){
            val staticPosition = staticPositionMapper.create(systemEvent.entityId)
            staticPosition.position = systemEvent.position
        }
    }

    fun removeEntity(systemEvent: SystemEvent.RemoveEntity) {
        entityMapper.remove(systemEvent.entityId)
        staticPositionMapper.remove(systemEvent.entityId)
        physicsMapper.remove(systemEvent.entityId)
        sizeMapper.remove(systemEvent.entityId)
        moveMapper.remove(systemEvent.entityId)
    }


    override fun process(entityId: Int) {

    }
}