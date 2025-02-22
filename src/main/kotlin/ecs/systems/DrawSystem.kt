package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import ecs.components.Zombie

@All(Zombie::class)
class DrawSystem: IteratingSystem() {

    private lateinit var zombies: ComponentMapper<Zombie>

    @Wire private lateinit var stage: Stage
    @Wire private lateinit var renderer: ShapeRenderer

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    override fun process(entityId: Int) {
        val zombie = zombies[entityId]
        zombies.get(entityId)

        renderer.projectionMatrix = stage.camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.RED
        renderer.circle(zombie.x, zombie.y, zombie.radius, 36)
        renderer.end()
    }

}