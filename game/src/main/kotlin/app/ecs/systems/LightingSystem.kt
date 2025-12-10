package app.ecs.systems

import app.di.GameViewport
import app.ecs.components.*
import app.ecs.models.GlobalAngle
import app.ecs.models.Player
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
import com.badlogic.gdx.math.MathUtils
import core.textures.createShadowShape
import models.entity.EntityType
import tools.eventbus.annotation.BusEvent
import kotlin.math.sqrt
import kotlin.random.Random

@All(EntityComponent::class)
class LightingSystem : IteratingSystem() {

    @Wire
    private lateinit var player: Player
    @Wire
    private lateinit var globalAngle: GlobalAngle
    @Wire
    private lateinit var camera: OrthographicCamera
    @Wire
    private lateinit var spriteBatch: SpriteBatch
    @Wire
    private lateinit var gameViewport: GameViewport

    private lateinit var lightBuffer: FrameBuffer
    private lateinit var lightTexture: Texture

    private lateinit var textureComponentMapper: ComponentMapper<TextureComponent>
    private lateinit var entityTypeComponentMapper: ComponentMapper<EntityTypeComponent>
    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>
    private lateinit var angleComponentMapper: ComponentMapper<AngleComponent>
    private lateinit var staticAngleComponentMapper: ComponentMapper<StaticAngleComponent>
    private lateinit var staticPositionComponentMapper: ComponentMapper<StaticPositionComponent>

    @BusEvent
    fun onResize(event: UiEvent.Resize){
        lightBuffer.dispose()

        lightBuffer = FrameBuffer(
            Pixmap.Format.RGBA8888,
            event.width,
            event.height,
            false
        )
    }

    override fun initialize() {
        lightBuffer = FrameBuffer(
            Pixmap.Format.RGBA8888,
            Gdx.graphics.width,
            Gdx.graphics.height,
            false
        )

        lightTexture = createShadowShape()
        lightTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        lightTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge)
    }

    override fun begin() {
        lightBuffer.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        spriteBatch.begin()

        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    }

    override fun process(entityId: Int) {
        val entityTypeComponent = entityTypeComponentMapper[entityId]?: return
        if (entityTypeComponent.entityType != EntityType.ENTITY) return

        val position = positionComponentMapper[entityId]?.getInterpolatedPosition() ?: return
        val size = sizeComponentMapper[entityId]?: return

        val worldLightSize = 7.9F + Random.nextFloat() * (8F - 7.9F)

        val dx = position.x
        val dy = position.y

        val cosAngle = MathUtils.cos(globalAngle.angle)
        val sinAngle = MathUtils.sin(globalAngle.angle)

        val rotatedX = dx * cosAngle - dy * sinAngle
        val rotatedY = dx * sinAngle + dy * cosAngle

        spriteBatch.color = Color.WHITE
        spriteBatch.draw(
            lightTexture,
            (rotatedX - worldLightSize / 2),
            (rotatedY - worldLightSize / 2) + size.halfHeight,
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