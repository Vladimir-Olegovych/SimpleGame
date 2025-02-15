package ecs.systems

import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import ecs.actions.MoveVelocity

@All
class CameraSystem: IteratingSystem() {

    @Wire private lateinit var stage: Stage
    @Wire private lateinit var moveVelocity: MoveVelocity

    override fun process(entityId: Int) {
        val velocity = moveVelocity.getVelocity()
        stage.camera.position.x += velocity.x
        stage.camera.position.y += velocity.y
    }

    override fun end() {
        stage.camera.update()
    }
}