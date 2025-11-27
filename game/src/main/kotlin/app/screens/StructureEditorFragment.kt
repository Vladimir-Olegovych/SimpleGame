package app.screens

import app.di.modules.UiViewport
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
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import core.textures.SkinID
import tools.graphics.screens.fragment.Fragment
import tools.graphics.setOnClickListener
import javax.inject.Inject

class StructureEditorFragment(
    private val navigation: Navigation.StructureEditor,
    private val onBack: () -> Unit
): Fragment() {

    @Inject lateinit var stage: Stage
    @Inject lateinit var camera: OrthographicCamera
    @Inject lateinit var spriteBatch: SpriteBatch
    @Inject lateinit var assetManager: AssetManager
    @Inject lateinit var uiViewport: UiViewport

    private lateinit var cellTexture: TextureRegion
    private lateinit var emptyCellTexture: TextureRegion

    private val gridTable = Table().apply { pad(10f) }
    private val menuTable = Table().apply { top() }

    private val cellSize = 64f
    private val gridWidth = 12
    private val gridHeight = 8

    init { Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F) }

    override fun onCreate(game: Game) {
        val skin = assetManager.get<Skin>(SkinID.BUTTON.skin)

        cellTexture = assetManager.get<TextureAtlas>(SkinID.BLOCK.atlas).findRegion("ic_stone_block")
        emptyCellTexture = assetManager.get<TextureAtlas>(SkinID.BLOCK.atlas).findRegion("ic_grass_block")

        val mainTable = Table().apply {
            setFillParent(true)
        }

        val backButton = TextButton("go back", skin).setOnClickListener { onBack.invoke() }
        menuTable.add(backButton).height(40F).width(70F).padTop(8F).row()

        createGrid()

        mainTable.add(menuTable).expandX().fillX().row()
        mainTable.add(gridTable).expand().fill().pad(20f)

        stage.addActor(mainTable)
        Gdx.input.inputProcessor = stage

        val camera = stage.viewport.camera
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        camera.update()
    }

    private fun createGrid() {
        val background = TextureRegionDrawable(emptyCellTexture)
        for (y in 0 until gridHeight) {
            for (x in 0 until gridWidth) {
                val cellTable = Table().apply {
                    this@apply.background = background
                    setTouchable(Touchable.enabled)
                }
                cellTable.setOnClickListener {
                    val image = Image(cellTexture)
                    cellTable.add(image).size(cellSize, cellSize)
                }
                gridTable.add(cellTable).size(cellSize, cellSize).pad(1f)
            }
            gridTable.row()
        }
    }

    override fun onRender(deltaTime: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        uiViewport.apply()
        stage.act(deltaTime)
        stage.draw()
    }

    override fun onResize(width: Int, height: Int) {
        uiViewport.update(width, height, true)
    }

    override fun onDestroy() {
        Gdx.input.inputProcessor = null
        stage.clear()
    }
}