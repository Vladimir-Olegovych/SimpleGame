package ecs.features

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.Entity
import ecs.components.Player
import tools.artemis.features.Feature
import type.EntityType

object PlayerFeature: Feature() {

    private val player = Player()
    @Wire private lateinit var camera: OrthographicCamera
    private lateinit var entityMapper: ComponentMapper<Entity>

    fun getPlayer(): Player = player

    override fun initialize() {
        player.entityId = artemisWorld.create()
        val entity = entityMapper.create(player.entityId)
        entity.entityType = EntityType.PLAYER
    }

    override fun process(entityId: Int) {
        val entity = entityMapper[player.entityId]
        camera.position.set(entity.x, entity.y, 0F)
        camera.update()
    }
}