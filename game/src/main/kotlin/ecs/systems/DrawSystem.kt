package ecs.systems

import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import ecs.components.Entity
import ecs.features.PlayerFeature
import ecs.features.draw.EnemyDrawFeature
import ecs.features.draw.PlayerDrawFeature
import ecs.features.draw.WallDrawFeature

@All(Entity::class)
class DrawSystem : IteratingSystem() {
    override fun initialize() {
        PlayerFeature.initialize(world)
        PlayerDrawFeature.initialize(world)
        EnemyDrawFeature.initialize(world)
        WallDrawFeature.initialize(world)
    }

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F)
        PlayerFeature.notify(0)
    }

    override fun process(entityId: Int) {
        PlayerDrawFeature.notify(entityId)
        EnemyDrawFeature.notify(entityId)
        WallDrawFeature.notify(entityId)
    }
}