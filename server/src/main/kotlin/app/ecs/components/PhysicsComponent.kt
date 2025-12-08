package org.example.app.ecs.components

import app.ecs.components.sender.UpdatableData
import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

class PhysicsComponent: Component() {

    private var latestPositionX: Float? = null
    private var latestPositionY: Float? = null
    private var latestAngle: Float? = null

    private var body: Body? = null

    fun getBody(): Body? = body

    fun setBody(body: Body?){
        this.body = body
        latestPositionX = body?.position?.x
        latestPositionY = body?.position?.y
        latestAngle = body?.angle
    }

    val positionUpdater = object : UpdatableData<Vector2> {
        override fun hasUpdate(): Boolean {
            return (latestPositionX != body?.position?.x) ||
                    (latestPositionY != body?.position?.y)
        }
        override fun markAsUpdated() {
            latestPositionX = body?.position?.x
            latestPositionY = body?.position?.y
        }
        override fun getUpdate(): Vector2 { return body?.position?: Vector2.Zero }
        override fun getAll(): Vector2 { return body?.position?: Vector2.Zero }
    }

    val angleUpdater = object : UpdatableData<Float> {
        override fun hasUpdate(): Boolean { return latestAngle != body?.angle }
        override fun markAsUpdated() { latestAngle = body?.angle }
        override fun getUpdate(): Float { return body?.angle?: 0F }
        override fun getAll(): Float { return body?.angle?: 0F }
    }
}