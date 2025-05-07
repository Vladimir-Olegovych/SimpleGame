package ecs.features.draw

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ecs.components.Entity
import ecs.components.Size
import tools.artemis.features.Feature
import tools.graphics.textures.Textures
import type.EntityType

object WallDrawFeature: Feature() {

    @Wire private lateinit var spriteBatch: SpriteBatch
    @Wire private lateinit var camera: OrthographicCamera
    @Wire private lateinit var assetManager: AssetManager
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var sizeMapper: ComponentMapper<Size>

    private lateinit var texture: Texture

    override fun initialize() {
        texture = assetManager.get(Textures.FLOOR.path)
    }

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        val sizes = sizeMapper[entityId]?: return
        if (entity.entityType != EntityType.WALL) return

        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        spriteBatch.draw(
            texture,
            entity.position.x - sizes.halfWidth,
            entity.position.y - sizes.halfHeight,
            sizes.halfWidth * 2,
            sizes.halfHeight * 2
        )
        spriteBatch.end()
    }
}