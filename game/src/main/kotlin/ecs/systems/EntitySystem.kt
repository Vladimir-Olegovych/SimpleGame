package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.IntMap
import ecs.components.EntityModel
import ecs.components.Player
import ecs.components.Size
import model.Event
import models.eventbus.BusEvent
import tools.eventbus.annotation.EventCallback
import tools.eventbus.annotation.EventType
import type.EntityType

@All(EntityModel::class)
class EntitySystem(): IteratingSystem() {

    @Wire private lateinit var player: Player

    private val entityMap = IntMap<Int>()
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var entityMapper: ComponentMapper<EntityModel>

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
        val entity = entityMapper[entityMap[busEvent.event.entityId]]?: return
        val position = entity.position?: Vector2()
        position.x = busEvent.event.x
        position.y = busEvent.event.y
        entity.position = position
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
        entityMapper.remove(entityMap[entityId]?: return)
        entityMap.remove(entityId)
        sizeMapper.remove(entityId)

    }

    override fun initialize() {
        player.entityId = world.create()
        val entity = entityMapper.create(player.entityId)
        entity.entityType = EntityType.PLAYER
    }

    override fun begin() {
        super.begin()
    }

    override fun process(entityId: Int) {

    }


    override fun dispose() {


    }
}