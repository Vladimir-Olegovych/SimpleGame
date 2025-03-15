package ecs.components

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body

class Physical: Component() {
    var body: Body? = null
}