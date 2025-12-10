package app.ecs.systems

import app.ecs.components.PositionComponent
import app.ecs.components.SizeComponent
import app.ecs.models.GlobalAngle
import app.ecs.models.IsometricMatrix
import app.ecs.models.Player
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3


class CameraSystem: GlobalAngle.Listener, BaseSystem() {

    @Wire private lateinit var player: Player
    @Wire private lateinit var camera: OrthographicCamera
    @Wire private lateinit var globalAngle: GlobalAngle
    @Wire private lateinit var isometricMatrix: IsometricMatrix

    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>


    override fun initialize() {
        globalAngle.addListener(this)
    }

    private fun getEntityPosition(angle: Float, entityId: Int): Pair<Float, Float>? {
        val position = positionComponentMapper[entityId]?.getInterpolatedPosition() ?: return null
        val size = sizeComponentMapper[player.entityId]?: return null
        val cosAngle = MathUtils.cos(angle)
        val sinAngle = MathUtils.sin(angle)

        val rotatedX = position.x * cosAngle - position.y * sinAngle
        val rotatedY = (position.x * sinAngle + position.y * cosAngle) + size.halfHeight
        return Pair(rotatedX, rotatedY)
    }

    override fun onRotationStart(currentAngle: Float, startAngle: Float) {}

    override fun onRotate(angle: Float) {
        val position = getEntityPosition(globalAngle.angle, player.entityId)?: return
        val x = position.first
        val y = position.second
        camera.position.set(Vector3(x, y, 0f))
        camera.update()
        isometricMatrix.updateMatrix()
    }

    override fun onRotationEnd(finalAngle: Float, startAngle: Float) {}

    override fun processSystem() {
        if (globalAngle.isRotating()) return
        val position = getEntityPosition(globalAngle.angle, player.entityId)?: return
        val x = position.first
        val y = position.second
        camera.position.lerp(Vector3(x, y, 0f), 0.5f)
        camera.update()
        isometricMatrix.updateMatrix()
    }

    override fun end() {
        globalAngle.process(world.delta)
    }

}
