package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.utils.IntMap
import ecs.components.EntityModel
import ecs.components.EntityPosition
import ecs.components.Player
import ecs.components.Size
import event.Event
import type.EntityType

@All(EntityModel::class)
class EntitySystem(): IteratingSystem() {

    @Wire private lateinit var player: Player

    private var maxDistance = Float.MAX_VALUE
    private val entityMap = IntMap<Int>()
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var entityPositionMapper: ComponentMapper<EntityPosition>
    private lateinit var sizeMapper: ComponentMapper<Size>

    fun setEntity(event: Event.Entity){
        val entity: EntityModel
        if (entityMap[event.entityId] == null) {
            val newId = world.create()
            entity = entityMapper.create(newId)
            entityMap.put(event.entityId, newId)
        } else {
            entity = entityMapper[entityMap[event.entityId]]
        }
        entity.entityType = event.entityType
    }

    fun setPosition(event: Event.Position){
        val entityPosition = entityPositionMapper[entityMap[event.entityId]]?: run {
            entityPositionMapper.create(entityMap[event.entityId])
        }
        entityPosition.setPosition(event.x, event.y)
    }

    fun setSize(event: Event.Size){
        val size = sizeMapper[entityMap[event.entityId]]?: run {
            sizeMapper.create(entityMap[event.entityId])
        }
        size.radius = event.radius
        size.halfWidth = event.halfWidth
        size.halfHeight = event.halfHeight
    }

    fun setChunkParams(event: Event.CurrentChunkParams){
        maxDistance = (event.chunkSize * event.chunkRadius) * 2 + event.chunkRadius
    }


    fun setRemove(event: Event.Remove){
        removeEntity(event.entityId)
    }

    fun setCurrentPlayer(event: Event.CurrentPlayer){
        entityMap.put(event.entityId, player.entityId)
    }

    private fun removeEntity(entityId: Int){
        entityMapper.remove(entityId)
        sizeMapper.remove(entityId)
        entityPositionMapper.remove(entityId)
        val iterator = entityMap.iterator()
        for(entry in iterator) {
            if (entry.value != entityId) continue
            iterator.remove()
            return
        }
    }

    private fun processRemove(entityId: Int){
        if (player.entityId == entityId) return
        val entityPosition = entityPositionMapper[entityId]?.getServerPosition()?: return
        val playerPosition = entityPositionMapper[player.entityId]?.getServerPosition()?: return

        val dx = entityPosition.x - playerPosition.x
        val dy = entityPosition.y - playerPosition.y
        val distanceSquared = dx * dx + dy * dy

        if (distanceSquared < maxDistance * maxDistance) return
        removeEntity(entityId)
    }

    override fun initialize() {
        player.entityId = world.create()
        val entity = entityMapper.create(player.entityId)
        entity.entityType = EntityType.PLAYER
    }

    override fun process(entityId: Int) {
        processRemove(entityId)
    }


    override fun dispose() {


    }
}