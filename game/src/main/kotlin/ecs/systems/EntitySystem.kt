package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.utils.IntMap
import ecs.components.*
import event.Event
import tools.eventbus.annotation.BusEvent

@All(EntityModel::class)
class EntitySystem(): IteratingSystem() {

    @Wire private lateinit var player: Player
    private val entityMap = IntMap<Int>()
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var entityPositionMapper: ComponentMapper<EntityPosition>
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var angleMapper: ComponentMapper<EntityAngle>

    @BusEvent
    fun setEntity(event: Event.Entity){
        val entity: EntityModel
        if (entityMap[event.entityId] == null) {
            val newId = world.create()
            entity = entityMapper.create(newId)
            entityMap.put(event.entityId, newId)
        } else {
            entity = entityMapper[entityMap[event.entityId]]
        }
        entity.isStatic = event.isStatic
        entity.entityType = event.entityType
        entity.textureType = event.textureType
        updateEntityTime(entityMap[event.entityId])
    }

    @BusEvent
    fun setPosition(event: Event.Position){
        val entityId = entityMap[event.entityId]
        val entityPosition = entityPositionMapper[entityId]?: run {
            entityPositionMapper.create(entityId)
        }
        entityPosition.setPosition(event.x, event.y)
        updateEntityTime(entityId)
    }

    @BusEvent
    fun setSize(event: Event.Size){
        val entityId = entityMap[event.entityId]
        val size = sizeMapper[entityId]?: run {
            sizeMapper.create(entityId)
        }
        size.radius = event.radius
        size.halfWidth = event.halfWidth
        size.halfHeight = event.halfHeight
        updateEntityTime(entityId)
    }

    @BusEvent
    fun setAngle(event: Event.Angle){
        val entityId = entityMap[event.entityId]
        val angle = angleMapper[entityId]?: run {
            angleMapper.create(entityId)
        }
        angle.setAngle(event.angle)
        updateEntityTime(entityId)
    }

    private var maxDistance = Float.MAX_VALUE
    @BusEvent
    fun setChunkParams(event: Event.CurrentChunkParams){
        maxDistance = (event.chunkSize * event.chunkRadius) * 2 + event.chunkRadius
    }

    @BusEvent
    fun setRemove(event: Event.Remove){
        removeEntity(event.entityId)
    }

    @BusEvent
    fun setCurrentPlayer(event: Event.CurrentPlayer){
        entityMap.put(event.entityId, player.entityId)
    }

    private fun updateEntityTime(entityId: Int){
        val entity = entityMapper[entityId]
        entity.updateTime = System.currentTimeMillis()
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

        val entity = entityMapper[entityId]?: return
        if (!entity.isStatic && System.currentTimeMillis() - entity.updateTime > ENTITY_TIMEOUT) {
            removeEntity(entityId)
            return
        }

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
        entityMapper.create(player.entityId)
    }

    override fun process(entityId: Int) {
        processRemove(entityId)
    }


    override fun dispose() {


    }

    companion object {
        const val ENTITY_TIMEOUT = 500L
    }
}