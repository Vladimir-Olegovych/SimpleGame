package org.example.ecs.event

import alexey.tools.common.collections.IntCollection
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.Connection
import models.TextureType
import org.example.core.models.BodyType
import type.EntityType

sealed class SystemEvent {
    data class CreateClient(val entityId: Int, val connection: Connection): SystemEvent()
    data class RemoveClient(val entityId: Int): SystemEvent()

    data class CreateEntity(val entityId: Int,
                            val entityType: EntityType,
                            val textureType: TextureType,
                            val isObserver: Boolean,
                            val isPhysical: Boolean,
                            val drawStats: Boolean = true,
                            val staticPosition: Vector2? = null,
                            val entityStats: Map<String, Any>? = null): SystemEvent()

    data class RemoveEntity(val entityId: Int): SystemEvent()

    data class CreateBody(val entityId: Int,
                          val isEnabled: Boolean,
                          val linearDamping: Float = 1F,
                          val angularDamping: Float = 1F,
                          val density: Float = 0.2F,
                          val friction: Float = 1.3F,
                          val restitution: Float = 1F,
                          val bodyType: BodyType,
                          val vector2: Vector2
    ): SystemEvent()

    data class RemoveBody(val entityId: Int): SystemEvent()
    data class PauseBody(val entityId: Int): SystemEvent()
    data class ResumeBody(val entityId: Int): SystemEvent()

    data class ApplyEntityToChunk(val entityId: Int, val vector2: Vector2): SystemEvent()
    data class RemoveEntityChunk(val entityId: Int): SystemEvent()

    data class ShowEntities(val entityId: Int, val entities: IntCollection): SystemEvent()
    data class HideEntities(val entityId: Int, val entities: IntCollection): SystemEvent()
}