package app.ecs.systems

import app.di.GameViewport
import app.ecs.components.*
import app.ecs.models.Player
import app.entity.draw.DrawableEntity
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import java.util.*

@All(EntityComponent::class)
class DrawSystem : IteratingSystem() {

    @Wire
    private lateinit var player: Player
    @Wire
    private lateinit var camera: OrthographicCamera
    @Wire
    private lateinit var spriteBatch: SpriteBatch
    @Wire
    private lateinit var gameViewport: GameViewport
    @Wire
    private lateinit var assetManager: AssetManager

    private lateinit var textureComponentMapper: ComponentMapper<TextureComponent>
    private lateinit var entityTypeComponentMapper: ComponentMapper<EntityTypeComponent>
    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>
    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>
    private lateinit var angleComponentMapper: ComponentMapper<AngleComponent>

    private val drawQueue = PriorityQueue<DrawableEntity>()

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255F/255F, 1F)

        val position = positionComponentMapper[player.entityId]?.getServerPosition() ?: return

        camera.position.lerp(Vector3(position.x, position.y, 0f), 0.1f)
        camera.update()
        gameViewport.apply()

        drawQueue.clear()
    }


    override fun process(entityId: Int) {
        val position = positionComponentMapper[entityId]?.getInterpolatedPosition() ?: return
        val entityType = entityTypeComponentMapper[entityId]?.entityType ?: return

        drawQueue.add(DrawableEntity(
            entityId = entityId,
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
            drawTexture(drawable.entityId)
        }
        spriteBatch.end()
    }

    private fun drawTexture(entityId: Int) {
        val entity = entityComponentMapper[entityId] ?: return
        val size = sizeComponentMapper[entityId] ?: return
        val texture = textureComponentMapper[entityId]?.textureRegion ?: return
        val position = positionComponentMapper[entityId]?.let {
            if (entity.isStatic) it.getServerPosition() else it.getInterpolatedPosition()
        } ?: return
        val angle = angleComponentMapper[entityId]?.getInterpolatedAngle() ?: 0F
        val epsilon = 0.01f

        spriteBatch.draw(
            texture,
            position.x - size.halfWidth,
            position.y,
            size.halfWidth,
            size.halfHeight,
            size.width + epsilon,
            size.height + epsilon,
            1f,
            1f,
            angle * MathUtils.radiansToDegrees
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