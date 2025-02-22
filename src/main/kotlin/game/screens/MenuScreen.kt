package game.screens

import client.models.User
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import eventbus.Events
import eventbus.GameEventBus
import game.MainGame
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tools.eventbus.EventBus
import tools.fillDraw
import tools.screens.screen.ScreenContext
import tools.setOnChange
import tools.textures.SkinID

class MenuScreen(private val game: MainGame): KoinComponent, ScreenContext() {

    private val stage by inject<Stage>()
    private val spriteBatch by inject<SpriteBatch>()
    private val manager by inject<AssetManager>()
    private val eventBus by inject<GameEventBus>()

    private val skin = manager.get<Skin>(SkinID.MAIN.path)

    private val menuTable = Table().apply {
        setFillParent(true)
        padTop(32F)
        top()
    }

    private val backgroundTexture = manager.get<TextureAtlas>("images/main.atlas").findRegion("menu")

    init {
        Gdx.gl.glClearColor(255F/255F, 255F/255F, 255/255F, 1F)
        eventBus.subscribe(Events.CONNECTED, object : EventBus.SubscribeEvent {
            override fun onEvent() {
                game.navHostController.navigate(GameScreen::class.java)
                eventBus.unSubscribe(this)
            }
        })

        val play = ImageButton(skin, "play").setOnChange {
            eventBus.connect("localhost", 5000, User("Boba1"))
        }
        menuTable.add(play).maxHeight(50F).maxWidth(180F).row()
        menuTable.add(Label("World of Fort Ships", skin))
    }

    override fun onAttached() {
        Gdx.input.inputProcessor = stage
        stage.addActor(menuTable)

        val camera = stage.viewport.camera
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        camera.update()
    }

    override fun onDetached() {
        Gdx.input.inputProcessor = null
        stage.clear()
    }
    override fun onPause() {
        println("onPause MenuScreen")
    }

    override fun onResume() {
        println("onResume MenuScreen")
    }

    override fun onResize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun onRender(deltaTime: Float) {
        stage.act(Gdx.graphics.deltaTime)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        spriteBatch.projectionMatrix = stage.camera.combined
        spriteBatch.begin()
        spriteBatch.fillDraw(backgroundTexture, stage.camera)
        spriteBatch.end()
        stage.draw()
    }

    override fun dispose() {
        super.dispose()
        println("dispose MenuScreen")
    }
}