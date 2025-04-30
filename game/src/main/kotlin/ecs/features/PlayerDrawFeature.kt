package ecs.features

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ecs.components.Entity
import tools.artemis.features.Feature

object PlayerDrawFeature: Feature() {

    @Wire private lateinit var renderer: ShapeRenderer
    @Wire private lateinit var camera: OrthographicCamera
    private lateinit var entityMapper: ComponentMapper<Entity>

    override fun initialize() {}

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]

        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.RED
        renderer.circle(entity.x, entity.y, 1F, 36)
        renderer.end()
    }
}