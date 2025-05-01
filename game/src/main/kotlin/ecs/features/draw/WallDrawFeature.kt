package ecs.features.draw

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ecs.components.Entity
import ecs.components.Wall
import tools.artemis.features.Feature
import tools.graphics.textures.Textures

object WallDrawFeature: Feature() {

    @Wire private lateinit var spriteBatch: SpriteBatch
    @Wire private lateinit var camera: OrthographicCamera
    @Wire private lateinit var assetManager: AssetManager
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var wallMapper: ComponentMapper<Wall>

    private lateinit var texture: Texture

    override fun initialize() {
        texture = assetManager.get(Textures.FLOOR.path)
    }

    override fun process(entityId: Int) {
        val wall = wallMapper[entityId]?: return
        val entity = entityMapper[entityId]

        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        spriteBatch.draw(
            texture,
            entity.x - wall.halfWidth,
            entity.y - wall.halfHeight,
            wall.halfWidth * 2,
            wall.halfHeight * 2
        )
        spriteBatch.end()
    }
}