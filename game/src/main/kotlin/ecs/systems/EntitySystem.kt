package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.utils.IntMap
import ecs.components.EntityModel
import ecs.components.Player
import ecs.components.EntityPosition
import ecs.components.Size
import event.Event
import models.eventbus.BusEvent
import tools.eventbus.annotation.EventCallback
import tools.eventbus.annotation.EventType
import type.EntityType

@All(EntityModel::class)
class EntitySystem(): IteratingSystem() {

    @Wire private lateinit var player: Player

    private var maxDistance = 32F
    private val entityMap = IntMap<Int>()
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var entityPositionMapper: ComponentMapper<EntityPosition>
    private lateinit var sizeMapper: ComponentMapper<Size>

    @EventType(BusEvent.FIELD_EVENT)
    @EventCallback
    private fun onReceiveEntity(busEvent: BusEvent.OnReceive<Event.Entity>){
        val entity: EntityModel
        if (entityMap[busEvent.event.entityId] == null) {
            val newId = world.create()
            entity = entityMapper.create(newId)
            entityMap.put(busEvent.event.entityId, newId)
        } else {
            entity = entityMapper[entityMap[busEvent.event.entityId]]
        }
        entity.entityType = busEvent.event.entityType
    }

    @EventType(BusEvent.FIELD_EVENT)
    @EventCallback
    private fun onReceivePosition(busEvent: BusEvent.OnReceive<Event.Position>){
        val entityPosition = entityPositionMapper[entityMap[busEvent.event.entityId]]?: run {
            entityPositionMapper.create(entityMap[busEvent.event.entityId])
        }
        entityPosition.setPosition(busEvent.event.x, busEvent.event.y)
    }

    @EventType(BusEvent.FIELD_EVENT)
    @EventCallback
    private fun onReceiveSize(busEvent: BusEvent.OnReceive<Event.Size>){
        val size = sizeMapper[entityMap[busEvent.event.entityId]]?: run {
            sizeMapper.create(entityMap[busEvent.event.entityId])
        }
        size.radius = busEvent.event.radius
        size.halfWidth = busEvent.event.halfWidth
        size.halfHeight = busEvent.event.halfHeight
    }

    @EventType(BusEvent.FIELD_EVENT)
    @EventCallback
    private fun onReceiveCurrentChunkParams(busEvent: BusEvent.OnReceive<Event.CurrentChunkParams>){
        maxDistance = (busEvent.event.chunkSize * busEvent.event.chunkRadius) * 2 + busEvent.event.chunkRadius
    }

    @EventType(BusEvent.FIELD_EVENT)
    @EventCallback
    private fun onReceiveRemove(busEvent: BusEvent.OnReceive<Event.Remove>){
        removeEntity(busEvent.event.entityId)
    }

    @EventType(BusEvent.FIELD_EVENT)
    @EventCallback
    private fun onReceiveCurrentPlayer(busEvent: BusEvent.OnReceive<Event.CurrentPlayer>){
        entityMap.put(busEvent.event.entityId, player.entityId)
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