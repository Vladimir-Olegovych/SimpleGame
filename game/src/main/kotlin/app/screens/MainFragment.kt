package app.screens

import app.navigation.Navigation
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import tools.graphics.fillDraw
import tools.graphics.screens.fragment.Fragment
import tools.graphics.setOnClickListener
import tools.graphics.textures.SkinID
import javax.inject.Inject

class MainFragment(
    private val navigation: Navigation.Main,
    private val onStart: () -> Unit
): Fragment() {

    @Inject lateinit var stage: Stage
    @Inject lateinit var viewport: Viewport
    @Inject lateinit var camera: OrthographicCamera
    @Inject lateinit var spriteBatch: SpriteBatch
    @Inject lateinit var assetManager: AssetManager

    private lateinit var backgroundTexture: TextureRegion
    init { Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F) }

    override fun onCreate(game: Game) {
        backgroundTexture = assetManager.get<TextureAtlas>("images/main.atlas").findRegion("menu")
        val skin = assetManager.get<Skin>(SkinID.MAIN.path)
        val menuTable = Table().apply {
            setFillParent(true)
            pad(1F)
            top()
        }

        val play = ImageButton(skin, "play").setOnClickListener {
            onStart.invoke()
        }

        menuTable.add(play).height(10F).width(30F).row()
        menuTable.add(Label("World of Fort Ships", skin))

        stage.addActor(menuTable)
        Gdx.input.inputProcessor = stage

        val camera = stage.viewport.camera
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        camera.update()
    }

    override fun onDestroy() {
        Gdx.input.inputProcessor = null
        stage.clear()
    }

    override fun onRender(deltaTime: Float) {
        stage.act(Gdx.graphics.deltaTime)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        spriteBatch.fillDraw(backgroundTexture, camera)
        spriteBatch.end()
        stage.draw()
    }

    override fun onResize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
}