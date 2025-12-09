package app.ecs.systems

import app.ecs.components.PositionComponent
import app.ecs.models.GlobalAngle
import app.ecs.models.Player
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3

class CameraSystem: BaseSystem() {
    @Wire private lateinit var player: Player
    @Wire private lateinit var camera: OrthographicCamera
    @Wire private lateinit var globalAngle: GlobalAngle

    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>

    private var lastAngle = 0F

    override fun initialize() {
        lastAngle = globalAngle.angle
    }

    override fun begin() {
        globalAngle.deltaTime = world.delta
        globalAngle.process()
    }
    
    override fun processSystem() {
        val position = positionComponentMapper[player.entityId]?.getInterpolatedPosition() ?: return
        val cosAngle = MathUtils.cos(globalAngle.angle)
        val sinAngle = MathUtils.sin(globalAngle.angle)

        val rotatedX = position.x * cosAngle - position.y * sinAngle
        val rotatedY = position.x * sinAngle + position.y * cosAngle

        if (lastAngle != globalAngle.angle) {
            camera.position.set(Vector3(rotatedX, rotatedY, 0f))
            lastAngle = globalAngle.angle
        } else {
            camera.position.lerp(Vector3(rotatedX, rotatedY, 0f), 0.1f)
        }
        camera.update()
    }


}