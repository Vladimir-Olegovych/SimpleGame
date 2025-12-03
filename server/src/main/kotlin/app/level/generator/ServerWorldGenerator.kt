package app.level.generator

import alexey.tools.common.level.Chunk
import app.level.biome.Biome
import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.math.Vector2
import core.models.components.texture.TextureContainer
import models.entity.EntityType
import models.textures.TextureType
import org.example.app.ecs.components.EntityComponent
import org.example.app.ecs.utils.utCreateEntity
import org.example.core.models.settings.ServerPreference
import org.koin.core.component.KoinComponent
import tools.chunk.WorldGenerator
import tools.math.getWorldPosition
import tools.noice.simplex.SimplexNoise
import kotlin.random.Random

class ServerWorldGenerator(
    private val artemisWorld: World,
    private val serverPreference: ServerPreference
): KoinComponent, WorldGenerator(
    chunkSize = serverPreference.chunkSize,
    blockSize = serverPreference.blockSize
){
    private val seed: Long = Random.nextLong()

    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>

    private val biomeNoise = SimplexNoise(seed)

    init {
        println("World seed: $seed")
        artemisWorld.inject(this)
    }

    override fun onGenerateChunk(chunk: Chunk, positions: Array<Vector2>) {
        /*
        val chunkPosition = chunk.getPosition()
        val seed = (chunkPosition.x.toLong() shl 32) or (chunkPosition.y.toLong() and 0xFFFFFFFF)
        val random = Random(seed + this.seed)
         */

        val chunkPosition = chunk.getPosition()

        for (position in positions) {
            val biome = getPositionBiome(position)
            val entityId = artemisWorld.create()
            artemisWorld.utCreateEntity(
                entityId = entityId,
                texture = TextureContainer.get(biome.block),
                entityType = EntityType.FLOOR,
                isObserver = false,
                isPhysical = false,
                staticPosition = position
            )
            val entityComponent = entityComponentMapper[entityId]
            chunk.add(entityId, entityComponent.isObserver)
        }
    }

    fun getPositionBiome(position: Vector2): Biome {
        val scale = 0.003f
        val noise = biomeNoise.noise2D(position.x, position.y, scale)

        val normalizedNoise = (noise + 1f) / 2f


        return when {
            normalizedNoise < 0.4f -> Biome.OCEAN
            normalizedNoise > 0.7f -> Biome.DESERT
            else -> Biome.FOREST
        }
    }

}