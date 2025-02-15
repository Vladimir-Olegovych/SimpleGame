package ecs.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

class Image: Component() {
    var color: Color = Color.RED
    var position = Vector2()
    var radius = 1F
}