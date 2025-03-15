package ecs.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Color

class Shape: Component() {
    var color: Color = Color.GOLD
    var radius: Float = 0F
}