package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import ecs.actions.MoveVelocity
import ecs.components.Image
import ecs.entity.Player
import tools.fillDraw
import tools.textures.Textures

@All(Image::class)
class DrawSystem: IteratingSystem() {

    private lateinit var planks: ComponentMapper<Image>
    @Wire private lateinit var moveVelocity: MoveVelocity

    @Wire private lateinit var stage: Stage
    @Wire private lateinit var renderer: ShapeRenderer
    @Wire private lateinit var batch: SpriteBatch
    @Wire private lateinit var manager: AssetManager

    private lateinit var background: TextureRegion

    private val player = Player()

    override fun initialize() {
        player.entityId = world.create()
        val playerImage = planks.create(player.entityId)
        playerImage.radius = 100F

        background = TextureRegion(manager.get(Textures.FLOOR.path, Texture::class.java).apply {
            setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        })
    }


    override fun begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val velocity = moveVelocity.getVelocity()
        background.scroll(velocity.x / 10000F, -velocity.y / 10000F)

        renderer.projectionMatrix = stage.camera.combined
        batch.projectionMatrix = stage.camera.combined
    }

    override fun process(entityId: Int) {
        batch.begin()
        batch.fillDraw(background, stage.viewport.camera)
        batch.end()

        val image = planks[entityId]
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = image.color
        renderer.circle(image.position.x, image.position.y, image.radius, 36)
        renderer.end()
    }

    override fun end() {
        stage.draw()
    }
}