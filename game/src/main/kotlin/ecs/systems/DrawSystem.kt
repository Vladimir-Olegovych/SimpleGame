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
import com.badlogic.gdx.math.Vector3
import ecs.components.EntityModel
import ecs.components.Player
import ecs.components.Size

@All(EntityModel::class)
class DrawSystem : IteratingSystem() {

    @Wire private lateinit var player: Player
    @Wire private lateinit var renderer: ShapeRenderer
    @Wire private lateinit var camera: OrthographicCamera
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var sizeMapper: ComponentMapper<Size>


    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F)

        val entity = entityMapper[player.entityId]?: return
        val position = entity.position?: return
        camera.position.lerp(Vector3(position.x, position.y, 0f), 0.1f)
        camera.update()
    }

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        val size = sizeMapper[entityId]?: return
        val position = entity.position?: return

        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.BLUE
        renderer.circle(position.x, position.y, size.radius, 36)
        renderer.end()
    }
}