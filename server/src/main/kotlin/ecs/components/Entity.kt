package org.example.ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import tools.math.ImmutableIntVector2
import type.EntityType
import java.util.concurrent.ConcurrentLinkedQueue

class Entity: Component() {
    private val observers = ConcurrentLinkedQueue<Entity>()
    val moveForce = Vector2(0F, 0F)
    var isObserver = false
    var body: Body? = null
    var chunkPosition = ImmutableIntVector2.ZERO
    var entityType = EntityType.NULL

    fun isObservable(): Boolean = observers.isNotEmpty()

    fun removeObserver(observer: Entity){
        observers.remove(observer)
    }

    fun addObserver(observer: Entity){
        observers.add(observer)
    }
}