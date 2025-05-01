package ecs.features.draw

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ecs.components.Enemy
import ecs.components.Entity
import tools.artemis.features.Feature

object EnemyDrawFeature: Feature() {

    @Wire private lateinit var renderer: ShapeRenderer
    @Wire private lateinit var camera: OrthographicCamera
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var enemyMapper: ComponentMapper<Enemy>

    override fun initialize() {}

    override fun process(entityId: Int) {
        val enemy = enemyMapper[entityId]?: return
        val entity = entityMapper[entityId]

        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.BLUE
        renderer.circle(entity.x, entity.y, 1F, 36)
        renderer.end()
    }
}