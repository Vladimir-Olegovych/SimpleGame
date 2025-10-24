package ecs.components

import com.artemis.Component

class EntityAngle: Component() {

    private var angle = 0F

    fun getServerAngle(): Float = angle

    fun setAngle(angle: Float) {
        this.angle = angle
    }
}