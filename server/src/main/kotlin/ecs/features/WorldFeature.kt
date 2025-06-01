package org.example.ecs.features

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import org.example.ecs.components.Entity
import org.example.ecs.components.Radius
import org.example.ecs.components.Size
import org.example.models.FixtureType
import org.example.values.GameValues
import tools.artemis.features.Feature
import tools.chunk.Chunk
import tools.chunk.ChunkListener
import tools.chunk.ChunkManager
import tools.math.ImmutableIntVector2
import tools.math.IntVector2
import tools.physics.createCircleEntity
import tools.physics.createWall
import tools.physics.setSensorRadius
import type.EntityType

object WorldFeature: ChunkListener, Feature() {

    @Wire private lateinit var box2dWold: World

    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var radiusMapper: ComponentMapper<Radius>

    private lateinit var chunkManager: ChunkManager

    override fun onChunkCreate(chunk: Chunk) {
        for (x in 2 until 8) {
            for (y in 0 until 10) {
                createEnemy(x * 0.2F, y * 0.2F, chunk, radius = 0.1F)
            }
        }
    }

    override fun onChunkDisable(chunk: Chunk) {
        for (entityId in chunk.getEntities()){
            val entity = entityMapper[entityId]?: return
            post {
                val body = entity.body
                body?.isActive = false
            }
        }
    }

    override fun onChunkEnabled(chunk: Chunk) {
        for (entityId in chunk.getEntities()){
            val entity = entityMapper[entityId]?: return
            post {
                val body = entity.body
                body?.isActive = true
            }
        }
    }

    override fun initialize() {
        val preference = GameValues.getServerPreference()
        chunkManager = ChunkManager(
            processedRadius = preference.chunkRadius,
            chunkSize = preference.chunkSize.toVector2()
        ).apply { setChunkListener(this@WorldFeature) }
    }

    fun getAllChunksInRadius(vector2: ImmutableIntVector2, action: (Chunk) -> Unit) {
        val preference = GameValues.getServerPreference()
        for (x in vector2.x - preference.chunkRadius .. vector2.x + preference.chunkRadius){
            for (y in vector2.y - preference.chunkRadius .. vector2.y + preference.chunkRadius){
                val loadedChunk = chunkManager.getChunkByIndex(IntVector2(x, y))?: continue
                action.invoke(loadedChunk)
            }
        }
    }

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        val body = entity.body?: return
        val bodyChunk = chunkManager.getChunkByWorld(body.position)?: return
        if (bodyChunk.getPosition() == entity.chunkPosition) return
        if (entity.isObserver){
            println("move ${entity.chunkPosition}, ${bodyChunk.getPosition()}")
        }
        chunkManager.move(entityId, bodyChunk.getPosition(), entity.isObserver)
        entity.chunkPosition = bodyChunk.getPosition()

    }

    private fun applyEntityChunk(entityId: Int, chunk: Chunk, isObserver: Boolean){
        val entity = entityMapper[entityId]
        entity.chunkPosition = chunk.getPosition()
        chunk.addEntity(entityId)
        if (isObserver) chunk.addObserver(entityId)
    }

    fun createWall(x: Float = 0F,
                   y: Float = 0F,
                   chunk: Chunk,
                   halfWidth: Float = 0.5F,
                   halfHeight: Float = 0.5F
    ) {
        val entityId = artemisWorld.create()
        val entity = entityMapper.create(entityId)
        val sizes = sizeMapper.create(entityId)
        sizes.halfWidth = halfWidth
        sizes.halfHeight = halfHeight
        entity.entityType = EntityType.WALL
        post {
            val body = box2dWold.createWall(
                x = x + chunk.getWorldPosition().x,
                y = y + chunk.getWorldPosition().y,
                userData = FixtureType.Entity(entityId),
                halfWidth = halfWidth,
                halfHeight = halfHeight,
            )
            entity.body = body
        }
    }

    fun createEnemy(x: Float = 0F,
                    y: Float = 0F,
                    chunk: Chunk,
                    restitution: Float = 1F,
                    radius: Float = 0.1F,
                    linearDamping: Float = 0.2F,
                    angularDamping: Float = 0.2F
    ) {
        val entityId = artemisWorld.create()
        val radiusEntity = radiusMapper.create(entityId)
        radiusEntity.radius = radius
        val entity = entityMapper.create(entityId)
        entity.entityType = EntityType.ENEMY
        post {
            val body = box2dWold.createCircleEntity(
                x = x + chunk.getWorldPosition().x,
                y = y + chunk.getWorldPosition().y,
                userData = FixtureType.Entity(entityId),
                restitution = restitution,
                radius = radius,
                linearDamping = linearDamping,
                angularDamping = angularDamping
            )
            entity.body = body
        }
        applyEntityChunk(entityId, chunk, false)
    }

    fun createPlayer(entityId: Int,
                     x: Float = 0F,
                     y: Float = 0F,
                     restitution: Float = 1F,
                     radius: Float = 0.15F,
                     linearDamping: Float = 0.1F,
                     angularDamping: Float = 0.1F
    ) {
        val chunk = chunkManager.createChunk(IntVector2(0, 0))
        val entity = entityMapper.create(entityId)
        val radiusEntity = radiusMapper.create(entityId)
        radiusEntity.radius = radius
        entity.entityType = EntityType.PLAYER
        entity.isObserver = true
        post {
            val body = box2dWold.createCircleEntity(
                x = x + chunk.getWorldPosition().x,
                y = y + chunk.getWorldPosition().y,
                userData = FixtureType.Entity(entityId),
                restitution = restitution,
                radius = radius,
                linearDamping = linearDamping,
                angularDamping = angularDamping
            )
            body.setSensorRadius(
                userData = FixtureType.Sensor(entityId),
                radius = GameValues.getServerPreference().sensorRadius
            )
            entity.body = body
        }
        applyEntityChunk(entityId, chunk, true)
    }

    fun removeEntity(entityId: Int) {
        val entity = entityMapper[entityId]

        entity.body?.let { post { box2dWold.destroyBody(it) } }
        entity.body = null

        entityMapper.remove(entityId)
    }
}