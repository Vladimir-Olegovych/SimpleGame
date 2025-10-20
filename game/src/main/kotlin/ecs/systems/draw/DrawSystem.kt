package ecs.systems.draw

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
import ecs.components.EntityModel
import ecs.components.EntityPosition
import ecs.components.Player
import ecs.components.Size
import tools.collections.TypedLayeredList

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

    private lateinit var grassTexture: TextureAtlas.AtlasRegion
    private val drawQueue = TypedLayeredList<DrawLayer>().apply {
        setTypes(
            DrawLayer.Other::class.java,
            DrawLayer.Entity::class.java
        )
    }

    override fun initialize() {
        grassTexture = assetManager.get<TextureAtlas>("images/main.atlas").findRegion("ic_grass_block")
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

        if(entity.isStatic){
            drawQueue.add(DrawLayer.Other {drawTexture(entityId)})
        } else {
            drawQueue.add(DrawLayer.Entity {drawEntity(entityId)})
        }
    }

    override fun end() {
        drawQueue.forEach { drawLayer -> drawLayer.action.invoke() }
        drawQueue.clear()
    }

    private fun drawEntity(entityId: Int){
        val size = sizeMapper[entityId]?: return
        val position = entityPositionMapper[entityId]?.getServerPosition()?: return
        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.BLUE
        renderer.circle(position.x, position.y, size.radius, 36)
        renderer.end()
    }

    private fun drawTexture(entityId: Int){
        val size = sizeMapper[entityId]?: return
        val position = entityPositionMapper[entityId]?.getServerPosition()?: return
        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        spriteBatch.draw(
            grassTexture,
            position.x - size.halfWidth,
            position.y - size.halfHeight,
            size.halfWidth * 2,
            size.halfHeight * 2
        )
        spriteBatch.end()
    }
}