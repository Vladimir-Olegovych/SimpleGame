package ecs.features

import com.artemis.ComponentMapper
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.IntMap
import ecs.components.Entity
import ecs.components.Radius
import ecs.components.Size
import model.Event
import tools.artemis.features.Feature

object EntityInputFeature: Disposable, Feature() {

    private val entityMap = IntMap<Int>()
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var radiusMapper: ComponentMapper<Radius>
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

    fun onReceiveSize(data: Event.Size){
        val size = sizeMapper[entityMap[data.entityId]]?: run {
            sizeMapper.create(entityMap[data.entityId])
        }
        size.halfWidth = data.halfWidth
        size.halfHeight = data.halfHeight
    }

    fun onReceiveRadius(data: Event.Radius){
        val radius = radiusMapper[entityMap[data.entityId]]?: run {
            radiusMapper.create(entityMap[data.entityId])
        }
        radius.radius = data.radius
    }

    fun onReceiveRemove(data: Event.Remove){
        entityMapper.remove(entityMap[data.entityId])
        entityMap.remove(data.entityId)
        radiusMapper.remove(data.entityId)
        sizeMapper.remove(data.entityId)
    }

    fun onReceiveCurrentPlayer(data: Event.CurrentPlayer){
        entityMap.put(data.entityId, PlayerFeature.getPlayer().entityId)
    }

    override fun initialize() {}

    override fun process(entityId: Int) {

    }

    override fun dispose() {
        entityMap.clear()
    }
}