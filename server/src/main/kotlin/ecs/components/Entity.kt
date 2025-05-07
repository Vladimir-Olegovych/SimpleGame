package org.example.ecs.components

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body
import type.EntityType
import java.util.concurrent.ConcurrentLinkedQueue

class Entity: Component() {
    private val observers = ConcurrentLinkedQueue<Entity>()
    var isObserver = false
    var body: Body? = null
    var entityType = EntityType.NULL

    fun isObservable(): Boolean = observers.isNotEmpty()

    fun removeObserver(observer: Entity){
        observers.remove(observer)
    }

    fun addObserver(observer: Entity){
        observers.add(observer)
    }
}