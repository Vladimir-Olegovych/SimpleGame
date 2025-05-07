package ecs.features.draw

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ecs.components.Entity
import ecs.components.Radius
import tools.artemis.features.Feature
import type.EntityType

object EnemyDrawFeature: Feature() {

    @Wire private lateinit var renderer: ShapeRenderer
    @Wire private lateinit var camera: OrthographicCamera
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var radiusMapper: ComponentMapper<Radius>

    override fun initialize() {}

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        if (entity.entityType != EntityType.ENEMY) return
        val radius = radiusMapper[entityId]?: return

        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.BLUE
        renderer.circle(entity.x, entity.y, radius.radius, 36)
        renderer.end()
    }
}