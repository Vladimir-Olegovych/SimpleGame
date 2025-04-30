package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import ecs.components.Entity
import ecs.components.Player

@All(Entity::class)
class DrawSystem : IteratingSystem() {
    @Wire private lateinit var renderer: ShapeRenderer
    @Wire private lateinit var camera: OrthographicCamera

    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var playerMapper: ComponentMapper<Player>

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F)
    }

    override fun process(entityId: Int) {
        val player = playerMapper[entityId]
        val entity = entityMapper[entityId]

        if (player != null) {
            camera.position.set(Vector2(entity.x, entity.y), 0F)
            camera.update()
        }

        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = if (player != null) Color.RED else Color.BLUE
        renderer.circle(entity.x, entity.y, 1F, 36)
        renderer.end()
    }
}