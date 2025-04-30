package ecs.systems

import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import ecs.components.Entity
import ecs.features.EntityDrawFeature
import ecs.features.PlayerDrawFeature
import ecs.features.PlayerFeature

@All(Entity::class)
class DrawSystem : IteratingSystem() {
    private val player = PlayerFeature.getPlayer()

    override fun initialize() {
        PlayerFeature.initialize(world)
        PlayerDrawFeature.initialize(world)
        EntityDrawFeature.initialize(world)
    }

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F)
    }

    override fun process(entityId: Int) {
        PlayerFeature.notify(entityId)
        when(entityId) {
            player.entityId -> PlayerDrawFeature.notify(entityId)
            else -> EntityDrawFeature.notify(entityId)
        }
    }
}