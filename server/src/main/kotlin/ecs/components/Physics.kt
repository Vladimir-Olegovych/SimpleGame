package org.example.ecs.components

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body

class Physics: Component() {
    var body: Body? = null
}