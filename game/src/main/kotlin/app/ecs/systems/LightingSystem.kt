package app.ecs.systems

import app.ecs.components.EntityComponent
import app.ecs.components.EntityTypeComponent
import app.ecs.components.PositionComponent
import app.ecs.components.SizeComponent
import app.entity.translator.ServerEntityTranslator
import app.event.UiEvent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import core.textures.createShadowShape
import models.entity.EntityType
import tools.eventbus.annotation.BusEvent
import kotlin.random.Random

@All(EntityComponent::class)
class LightingSystem : IteratingSystem() {

    @Wire
    private lateinit var camera: OrthographicCamera
    @Wire
    private lateinit var spriteBatch: SpriteBatch
    @Wire
    private lateinit var serverEntityTranslator: ServerEntityTranslator

    private lateinit var lightBuffer: FrameBuffer
    private lateinit var lightTexture: Texture

    private lateinit var entityTypeComponentMapper: ComponentMapper<EntityTypeComponent>
    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>

    @BusEvent
    fun onResize(event: UiEvent.Resize){
        lightBuffer.dispose()
        lightBuffer = FrameBuffer(Pixmap.Format.RGBA8888, event.width, event.height, false)
    }

    override fun initialize() {
        lightBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)

        lightTexture = createShadowShape()
        lightTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        lightTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge)
    }

    override fun begin() {
        lightBuffer.begin()

        val lightPercent = calculateLightPercent()
        Gdx.gl.glClearColor(lightPercent, lightPercent, lightPercent, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        spriteBatch.begin()

        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    }

    private fun calculateLightPercent(): Float {
        val time = serverEntityTranslator.time
        val dayTime = serverEntityTranslator.dayTime
        val eveningTime = serverEntityTranslator.eveningTime
        val nightTime = serverEntityTranslator.nightTime
        val dawnTime = serverEntityTranslator.dawnTime

        if (time < dayTime) {
            return 1f
        }
        else if (time < dayTime + eveningTime) {
            val eveningProgress = (time - dayTime) / eveningTime
            return 1f - eveningProgress
        }
        else if (time < dayTime + eveningTime + nightTime) {
            return 0f
        }
        else {
            val dawnProgress = (time - (dayTime + eveningTime + nightTime)) / dawnTime
            return dawnProgress
        }
    }

    override fun process(entityId: Int) {
        //if (entityId != player.entityId) return
        val entityTypeComponent = entityTypeComponentMapper[entityId]?: return
        if (entityTypeComponent.entityType != EntityType.ENTITY) return

        val position = positionComponentMapper[entityId]?.getInterpolatedPosition() ?: return

        val worldLightSize = 7.9F + Random.nextFloat() * (8F - 7.9F)

        spriteBatch.color = Color.WHITE
        spriteBatch.draw(
            lightTexture,
            (position.x - worldLightSize / 2),
            (position.y - worldLightSize / 2),
            worldLightSize,
            worldLightSize
        )
    }

    override fun end() {
        spriteBatch.end()
        lightBuffer.end()

        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        spriteBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO)

        val region = TextureRegion(lightBuffer.colorBufferTexture)
        region.flip(false, true)

        spriteBatch.color = Color.WHITE
        spriteBatch.draw(
            region,
            camera.position.x - camera.viewportWidth / 2 * camera.zoom,
            camera.position.y - camera.viewportHeight / 2 * camera.zoom,
            camera.viewportWidth * camera.zoom,
            camera.viewportHeight * camera.zoom
        )

        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        spriteBatch.end()

    }

    override fun dispose() {
        lightBuffer.dispose()
        lightTexture.dispose()
    }
}