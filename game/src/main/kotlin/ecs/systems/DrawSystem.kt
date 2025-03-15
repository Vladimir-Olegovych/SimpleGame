package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import ecs.components.Body
import ecs.components.Shape
import ecs.components.Square

@All(Body::class)
class DrawSystem: IteratingSystem() {

    private lateinit var shapes: ComponentMapper<Shape>
    private lateinit var squares: ComponentMapper<Square>
    private lateinit var bodies: ComponentMapper<Body>

    @Wire private lateinit var stage: Stage
    @Wire private lateinit var renderer: ShapeRenderer

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    override fun process(entityId: Int) {
        val body = bodies[entityId]

        val shape = shapes[entityId]
        if (shape != null) {
            //Shape
            renderer.projectionMatrix = stage.camera.combined
            renderer.begin(ShapeRenderer.ShapeType.Filled)
            renderer.color = shape.color
            renderer.circle(body.x, body.y, shape.radius, 36)
            renderer.end()
        }

        val square = squares[entityId]
        if (square != null) {
            //Square
            renderer.projectionMatrix = stage.camera.combined
            renderer.begin(ShapeRenderer.ShapeType.Filled)
            renderer.color = square.color
            renderer.rect(body.x, body.y, square.halfWidth, square.halfHeight)
            renderer.end()
        }

    }

}