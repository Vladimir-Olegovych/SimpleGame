package app.ecs.systems

import app.di.GameViewport
import app.ecs.components.*
import app.ecs.models.Player
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import models.textures.SkinID
import models.entity.EntityType
import values.ApplicationValues
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
    private lateinit var statsComponentMapper: ComponentMapper<StatsComponent>
    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>
    private lateinit var angleComponentMapper: ComponentMapper<AngleComponent>

    private lateinit var font: BitmapFont

    private val drawQueue = mapOf(
        EntityType.FLOOR to LinkedList<Int>(),
        EntityType.ITEM to LinkedList<Int>(),
        EntityType.ENTITY to LinkedList<Int>(),
        EntityType.WALL to LinkedList<Int>(),
        EntityType.CEILING to LinkedList<Int>(),
        EntityType.NULL to LinkedList<Int>(),
    )

    override fun initialize() {
        val skin = assetManager.get<Skin>(SkinID.BUTTON.skin)
        font = skin.getFont("small")
    }

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F)

        val position = positionComponentMapper[player.entityId]?.getServerPosition()?: return
        camera.position.lerp(Vector3(position.x, position.y, 0f), 0.1f)
        camera.update()
        gameViewport.apply()
    }

    override fun process(entityId: Int) {
        val entityTypeComponent = entityTypeComponentMapper[entityId]?: return
        drawQueue[entityTypeComponent.entityType]?.add(entityId)
    }

    override fun end() {
        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        for(list in drawQueue.values){
            for (entityId in list){ drawTexture(entityId) }
            for (entityId in list){   drawStats(entityId) }
            list.clear()
        }
        spriteBatch.end()
    }

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

    private fun drawTexture(entityId: Int){
        val entity = entityComponentMapper[entityId]?: return
        val size = sizeComponentMapper[entityId]?: return
        val texture = textureComponentMapper[entityId]?.textureRegion?: return
        val position = positionComponentMapper[entityId]?.let {
            if (entity.isStatic) it.getServerPosition() else it.getInterpolatedPosition()
        }?: return
        val angle = angleComponentMapper[entityId]?.getInterpolatedAngle()?: 0F

        spriteBatch.draw(
            texture,
            position.x - size.halfWidth,
            position.y - size.halfHeight,
            size.halfWidth,
            size.halfHeight,
            size.halfWidth * 2 + 0.01F,
            size.halfHeight * 2 + 0.01F,
            1f,
            1f,
            angle * MathUtils.radiansToDegrees
        )
    }

    override fun dispose() {
        camera.position.set(0F, 0F, 0F)
        camera.update()
    }

    companion object {
        const val TEXT_PADDING_SCALER = 1F
    }
}