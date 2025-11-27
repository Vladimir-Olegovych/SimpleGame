package org.example.app.ecs.components

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body

class PhysicsComponent: Component() {
    var body: Body? = null
}