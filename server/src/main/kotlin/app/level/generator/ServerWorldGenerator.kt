package app.level.generator

import alexey.tools.common.level.Chunk
import app.items.DiamondItem
import app.level.biome.Biome
import com.artemis.ComponentMapper
import com.artemis.World
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.Vector2
import core.models.components.texture.TextureContainer
import models.entity.EntityType
import models.textures.TextureType
import org.example.app.ecs.components.EntityComponent
import org.example.app.ecs.components.SizeComponent
import org.example.app.ecs.utils.utCreateBody
import org.example.app.ecs.utils.utCreateEntity
import org.example.core.items.manager.ItemsManager
import org.example.core.models.box2d.BodyType
import org.example.core.models.settings.ServerPreference
import org.koin.core.component.KoinComponent
import tools.chunk.WorldGenerator
import tools.noice.simplex.SimplexNoise
import kotlin.math.abs
import kotlin.random.Random

class ServerWorldGenerator(
    private val artemisWorld: World,
    private val serverPreference: ServerPreference
): KoinComponent, WorldGenerator(
    chunkSize = serverPreference.chunkSize,
    blockSize = serverPreference.blockSize
){
    private val seed = Random.nextLong()

    @Wire private lateinit var itemsManager: ItemsManager
    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>

    private val biomeNoise = SimplexNoise(seed)

    init {
        println("World seed: $seed")
        artemisWorld.inject(this)
    }

    override fun onGenerateChunk(chunk: Chunk, positions: Array<Vector2>) {
        val chunkPosition = chunk.getPosition()
        val seed = (chunkPosition.x * 73856093L) xor (chunkPosition.y * 19349663L) xor this.seed
        val random = Random(seed)

        for (position in positions) {
            val biome = getPositionBiome(position)
            val entityId = artemisWorld.create()
            artemisWorld.utCreateEntity(
                entityId = entityId,
                texture = TextureContainer.get(biome.block),
                entityType = EntityType.BACKGROUND,
                isObserver = false,
                isPhysical = false,
                staticPosition = position
            )
            val entityComponent = entityComponentMapper[entityId]
            chunk.add(entityId, entityComponent.isObserver)

            //if (random.nextInt(0, 50) < 3)
                createEntity(chunk, position)
        }
    }

    fun getPositionBiome(position: Vector2): Biome {
        val scale = 0.003f
        val noise = biomeNoise.noise2DScaled(position.x, position.y, scale)
        val normalizedNoise = abs(noise)

        return when (normalizedNoise) {
            in 0F .. 0.4f -> Biome.OCEAN
            in 0.4f .. 0.5f -> Biome.DESERT
            else -> Biome.FOREST
        }
    }

    fun createEntity(chunk: Chunk, position: Vector2){
        val entityId = artemisWorld.create()
        artemisWorld.utCreateEntity(
            entityId = entityId,
            texture = TextureContainer.get(TextureType.ITEM.DIAMOND),
            entityType = EntityType.ENTITY,
            isObserver = false,
            isPhysical = true,
            worldItem = itemsManager.create(DiamondItem::class.java),
        )
        val size = sizeComponentMapper[entityId]
        //size.height = 3F
        artemisWorld.utCreateBody(
            entityId = entityId,
            vector2 = position,
            bodyType = BodyType.CIRCLE,
            isEnabled = false
        )
        val entityComponent = entityComponentMapper[entityId]
        chunk.add(entityId, entityComponent.isObserver)
    }

}