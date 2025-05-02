package ecs.features

import com.artemis.ComponentMapper
import com.badlogic.gdx.utils.IntMap
import ecs.components.Entity
import ecs.components.Size
import model.Event
import tools.artemis.features.Feature

object EntityInputFeature: Feature() {

    private val entityMap = IntMap<Int>()
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var entityMapper: ComponentMapper<Entity>

    fun onReceiveEntity(data: Event.Entity){
        val entity: Entity

        if (entityMap[data.entityId] == null) {
            val newId = artemisWorld.create()
            entity = entityMapper.create(newId)
            entityMap.put(data.entityId, newId)
        } else {
            entity = entityMapper[entityMap[data.entityId]]
        }

        entity.entityType = data.entityType
    }

    fun onReceivePosition(data: Event.Position){
        val entity = entityMapper[entityMap[data.entityId]]?: return

        entity.x = data.x
        entity.y = data.y
    }

    fun onReceiveSizes(data: Event.Size){
        val sizes = sizeMapper[entityMap[data.entityId]]?: run {
            sizeMapper.create(entityMap[data.entityId])
        }

        sizes.halfWidth = data.halfWidth
        sizes.halfHeight = data.halfHeight
    }

    fun onReceiveRemove(data: Event.Remove){
        entityMapper.remove(entityMap[data.entityId])
        entityMap.remove(data.entityId)
    }

    fun onReceiveCurrentPlayer(data: Event.CurrentPlayer){
        entityMap.put(data.entityId, PlayerFeature.getPlayer().entityId)
    }

    override fun initialize() {}

    override fun process(entityId: Int) {

    }
}