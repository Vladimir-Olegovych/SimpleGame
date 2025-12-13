package app.ecs.systems

import app.ecs.components.PositionComponent
import app.ecs.components.SizeComponent
import app.ecs.models.Player
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3


class CameraSystem: BaseSystem() {

    @Wire private lateinit var player: Player
    @Wire private lateinit var camera: OrthographicCamera

    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>

    override fun processSystem() {
        val position = positionComponentMapper[player.entityId]?.getInterpolatedPosition() ?: return
        val size = sizeComponentMapper[player.entityId]?: return
        camera.position.lerp(
            Vector3(
                position.x,
                position.y + size.halfHeight,
                0f
            ), 0.1f
        )
        camera.update()
    }

}
