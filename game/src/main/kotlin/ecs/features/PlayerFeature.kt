package ecs.features

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.Entity
import ecs.components.Player
import tools.artemis.features.Feature

object PlayerFeature: Feature() {

    private val player = Player()
    @Wire private lateinit var camera: OrthographicCamera
    private lateinit var entityMapper: ComponentMapper<Entity>

    fun getPlayer(): Player = player

    override fun initialize() {
        player.entityId = artemisWorld.create()
        entityMapper.create(player.entityId)
    }

    override fun process(entityId: Int) {
        val playerEntity = entityMapper[player.entityId]
        camera.position.set(playerEntity.x, playerEntity.y, 0F)
        camera.update()
    }
}