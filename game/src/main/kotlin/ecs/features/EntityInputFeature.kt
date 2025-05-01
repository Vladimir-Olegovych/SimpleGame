package ecs.features

import com.artemis.ComponentMapper
import com.badlogic.gdx.utils.IntMap
import ecs.components.Enemy
import ecs.components.Entity
import ecs.components.Wall
import model.Event
import tools.artemis.features.Feature

object EntityInputFeature: Feature() {

    private val entityMap = IntMap<Int>()
    private lateinit var enemyMapper: ComponentMapper<Enemy>
    private lateinit var wallMapper: ComponentMapper<Wall>
    private lateinit var entityMapper: ComponentMapper<Entity>

    fun onReceiveEnemy(data: Event.Enemy){
        val entity: Entity

        if (entityMap[data.entityId] == null) {
            val newId = artemisWorld.create()
            entity = entityMapper.create(newId)
            val enemy = enemyMapper.create(newId)
            entityMap.put(data.entityId, newId)
        } else {
            entity = entityMapper[entityMap[data.entityId]]
        }

        entity.x = data.x
        entity.y = data.y
    }

    fun onReceiveWall(data: Event.Wall){
        val entity: Entity
        val wall: Wall

        if (entityMap[data.entityId] == null) {
            val newId = artemisWorld.create()
            entity = entityMapper.create(newId)
            wall = wallMapper.create(newId)
            entityMap.put(data.entityId, newId)
        } else {
            entity = entityMapper[entityMap[data.entityId]]
            wall = wallMapper[entityMap[data.entityId]]
        }

        entity.x = data.x
        entity.y = data.y

        wall.halfWidth = data.halfWidth
        wall.halfHeight = data.halfHeight
    }

    fun onReceivePlayerDisconnected(data: Event.PlayerDisconnected){
        entityMapper.remove(entityMap[data.entityId])
        entityMap.remove(data.entityId)
    }

    fun onReceivePlayer(data: Event.Player){
        entityMap.put(data.entityId, PlayerFeature.getPlayer().entityId)
    }

    override fun initialize() {}

    override fun process(entityId: Int) {

    }
}