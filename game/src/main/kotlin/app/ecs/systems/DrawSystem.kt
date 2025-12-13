package app.ecs.systems

import app.di.GameViewport
import app.ecs.components.*
import app.entity.draw.DrawableEntity
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import java.util.*

@All(EntityComponent::class)
class DrawSystem : IteratingSystem() {

    @Wire
    private lateinit var camera: OrthographicCamera
    @Wire
    private lateinit var spriteBatch: SpriteBatch
    @Wire
    private lateinit var gameViewport: GameViewport

    private lateinit var textureComponentMapper: ComponentMapper<TextureComponent>
    private lateinit var entityTypeComponentMapper: ComponentMapper<EntityTypeComponent>
    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>
    private lateinit var angleComponentMapper: ComponentMapper<AngleComponent>
    private lateinit var staticAngleComponentMapper: ComponentMapper<StaticAngleComponent>
    private lateinit var staticPositionComponentMapper: ComponentMapper<StaticPositionComponent>

    private val drawQueue = PriorityQueue<DrawableEntity>()

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255F/255F, 1F)
        gameViewport.apply()
        drawQueue.clear()
    }

    override fun process(entityId: Int) {
        val entityType = entityTypeComponentMapper[entityId]?.entityType ?: return
        val position = positionComponentMapper[entityId]?.getInterpolatedPosition()?: staticPositionComponentMapper[entityId]?.position?: return

        drawQueue.add(DrawableEntity(
            entityId = entityId,
            xPosition = position.x,
            yPosition = position.y,
            entityType = entityType
        ))
    }

    override fun end() {
        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        spriteBatch.enableBlending()

        while (drawQueue.isNotEmpty()) {
            val drawable = drawQueue.poll()
            drawTexture(drawable)
        }
        spriteBatch.end()
    }

    private fun drawTexture(drawable: DrawableEntity) {
        val entityId = drawable.entityId
        val isStaticAngle = staticAngleComponentMapper[entityId] != null

        val angle = angleComponentMapper[entityId]?.getInterpolatedAngle()?: staticAngleComponentMapper[entityId]?.angle?: 0F
        val size = sizeComponentMapper[entityId] ?: return
        val texture = textureComponentMapper[entityId]?.textureRegion ?: return

        val epsilon = 0.01f
        val totalAngle = if (isStaticAngle) 0F else angle

        spriteBatch.draw(
            texture,
            drawable.xPosition - size.halfWidth,
            drawable.yPosition,
            size.halfWidth,
            size.halfHeight,
            size.width + epsilon,
            size.height + epsilon,
            1f,
            1f,
            totalAngle * MathUtils.radiansToDegrees
        )
    }

    override fun dispose() {
        camera.position.set(0F, 0F, 0F)
        camera.update()
        drawQueue.clear()
    }
}

/*
private fun drawStats(entityId: Int){
    val entity = entityComponentMapper[entityId]?: return
    if (!entity.drawStats) return
    val size = sizeComponentMapper[entityId]?: return
    val stats = statsComponentMapper[entityId]?: return
    val position = positionComponentMapper[entityId]?.let {
        if (entity.isStatic) it.getServerPosition() else it.getInterpolatedPosition()
    }?: return

    stats.getStat<Int>(ApplicationValues.Stats.HP)?.let { value ->
        val hpText = "$value hp"
        val glyphLayout = GlyphLayout(font, hpText)
        font.draw(
            spriteBatch,
            hpText,
            position.x - glyphLayout.width / 2,
            position.y - size.halfHeight * TEXT_PADDING_SCALER - glyphLayout.height / 2
        )
    }

    stats.getStat<String>(ApplicationValues.Stats.NAME)?.let { nameText ->
        val glyphLayout = GlyphLayout(font, nameText)
        font.draw(
            spriteBatch,
            nameText,
            position.x - glyphLayout.width / 2,
            position.y + size.halfHeight * TEXT_PADDING_SCALER + size.halfHeight * 1.5F - glyphLayout.height / 2
        )
    }
}
 */