package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import core.textures.SkinID
import ecs.components.EntityModel
import ecs.components.EntityPosition
import ecs.components.Player
import ecs.components.Size
import models.TextureType
import type.EntityType
import java.util.*

@All(EntityModel::class)
class DrawSystem : IteratingSystem() {

    @Wire
    private lateinit var player: Player
    @Wire
    private lateinit var renderer: ShapeRenderer
    @Wire
    private lateinit var camera: OrthographicCamera
    @Wire
    private lateinit var spriteBatch: SpriteBatch
    @Wire
    private lateinit var assetManager: AssetManager
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var entityPositionMapper: ComponentMapper<EntityPosition>
    private lateinit var sizeMapper: ComponentMapper<Size>

    private lateinit var textureMap: Map<TextureType, TextureAtlas.AtlasRegion>
    private val drawQueue = mapOf(
        EntityType.FLOOR to LinkedList<Int>(),
        EntityType.ENTITY to LinkedList<Int>(),
        EntityType.WALL to LinkedList<Int>(),
        EntityType.CEILING to LinkedList<Int>(),
        EntityType.NULL to LinkedList<Int>(),
    )

    override fun initialize() {
        textureMap = mapOf(

            TextureType.GRASS to assetManager.get<TextureAtlas>(SkinID.BLOCK.atlas).findRegion("ic_grass_block"),
            TextureType.LAVA to assetManager.get<TextureAtlas>(SkinID.BLOCK.atlas).findRegion("ic_lava_block"),
            TextureType.STONE to assetManager.get<TextureAtlas>(SkinID.BLOCK.atlas).findRegion("ic_stone_block"),
        )
    }

    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F)

        val position = entityPositionMapper[player.entityId]?.getServerPosition()?: return
        camera.position.lerp(Vector3(position.x, position.y, 0f), 0.1f)
        camera.update()
    }

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        drawQueue[entity.entityType]?.add(entityId)
    }

    override fun end() {
        for(list in drawQueue.values){
            for (entityId in list){
                drawEntity(entityId)
            }
            list.clear()
        }
    }

    private fun drawEntity(entityId: Int){
        val entity = entityMapper[entityId]?: return
        val size = sizeMapper[entityId]?: return
        val texture = textureMap[entity.textureType]
        val position = entityPositionMapper[entityId]?.let {
            if (entity.isStatic) it.getServerPosition() else it.getInterpolatedPosition()
        }?: return

        if (entity.entityType == EntityType.FLOOR) {
            spriteBatch.projectionMatrix = camera.combined
            spriteBatch.begin()
            spriteBatch.draw(
                texture,
                position.x - size.halfWidth,
                position.y - size.halfHeight,
                size.halfWidth * 2 + 0.01F,
                size.halfHeight * 2 + 0.01F
            )
            spriteBatch.end()
            return
        }
        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.BLUE
        renderer.circle(position.x, position.y, size.radius, 36)
        renderer.end()
    }
}