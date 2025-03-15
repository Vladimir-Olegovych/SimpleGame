package ecs.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Color

class Square: Component() {
    var color: Color = Color.RED
    var halfWidth: Float = 0F
    var halfHeight: Float = 0F
}