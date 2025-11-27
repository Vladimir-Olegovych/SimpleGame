package org.example.app.level.chunks

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.math.Vector2
import models.enums.EntityType
import models.enums.TextureType
import org.example.app.ecs.components.EntityComponent
import org.example.app.ecs.utils.utCreateEntity
import org.example.core.level.MultipleChunkGenerator

class FloorChunkGenerator(
    private val artemisWorld: World,
    private val chunkManager: AdvancedChunkManager
): MultipleChunkGenerator() {

    init { artemisWorld.inject(this) }

    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>

    override fun busyAfterGenerate(): Boolean {
        return false
    }

    override fun generatePositions(positions: Array<Vector2>): Boolean {
        positions.forEach { position ->
            val entityId = artemisWorld.create()
            artemisWorld.utCreateEntity(
                entityId = entityId,
                textureType = TextureType.GRASS,
                entityType = EntityType.FLOOR,
                isObserver = false,
                isPhysical = false,
                staticPosition = position
            )
            val entityComponent = entityComponentMapper[entityId]
            getChunk().add(entityId, entityComponent.isObserver)
        }
        return false
    }

}