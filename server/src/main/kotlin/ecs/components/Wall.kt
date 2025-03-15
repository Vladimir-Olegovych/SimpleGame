package ecs.components

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body

class Wall: Component() {
    var halfWidth: Float = 0F
    var halfHeight: Float = 0F
    var body: Body? = null
}