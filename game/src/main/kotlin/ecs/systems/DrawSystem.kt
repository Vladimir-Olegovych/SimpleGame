package ecs.systems

import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import ecs.components.Entity
import ecs.features.EnemyDrawFeature
import ecs.features.PlayerDrawFeature
import ecs.features.PlayerFeature

@All(Entity::class)
class DrawSystem : IteratingSystem() {
    override fun initialize() {
        PlayerFeature.initialize(world)
        PlayerDrawFeature.initialize(world)
        EnemyDrawFeature.initialize(world)
    }

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F)
    }

    override fun process(entityId: Int) {
        PlayerFeature.notify(entityId)
        PlayerDrawFeature.notify(entityId)
        EnemyDrawFeature.notify(entityId)
    }
}