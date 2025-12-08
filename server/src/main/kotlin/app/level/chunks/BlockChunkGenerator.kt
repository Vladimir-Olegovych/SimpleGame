package org.example.app.level.chunks

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.math.Vector2
import core.models.components.texture.TextureContainer
import models.entity.EntityType
import models.textures.TextureType
import org.example.app.ecs.components.EntityComponent
import org.example.app.ecs.utils.utCreateBody
import org.example.app.ecs.utils.utCreateEntity
import org.example.core.level.SingleChunkGenerator
import org.example.core.models.box2d.BodyType

class BlockChunkGenerator(
    private val artemisWorld: World,
    private val chunkManager: AdvancedChunkManager
): SingleChunkGenerator() {

    init { artemisWorld.inject(this) }

    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>

    override fun generatePosition(position: Vector2): Boolean {
        if (getRandom().nextInt(0, 40) > 2) return false

        val entityId = artemisWorld.create()
        artemisWorld.utCreateEntity(
            entityId = entityId,
            texture = TextureContainer.get(TextureType.BLOCK.STONE),
            entityType = EntityType.STRUCTURE,
            isObserver = false,
            isPhysical = true,
            staticPosition = position
        )
        artemisWorld.utCreateBody(
            entityId = entityId,
            vector2 = position,
            bodyType = BodyType.SQUARE,
            isEnabled = false
        )
        val entityComponent = entityComponentMapper[entityId]
        getChunk().add(entityId, entityComponent.isObserver)
        return true
    }
}