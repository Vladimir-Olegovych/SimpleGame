package ecs.components

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body

class Zombie: Component() {
    var radius: Float = 0F
    var body: Body? = null
}