package game

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import di.appModule
import eventbus.GameEventBus
import game.screens.GameScreen
import game.screens.MenuScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import tools.screens.navigation.NavHostController
import tools.screens.screen.ScreenContext
import tools.textures.SkinID
import tools.textures.Textures

class MainGame: Game(), KoinComponent {

    init {
        startKoin { modules(appModule) }
    }

    private val manager: AssetManager by inject()
    private val eventBus: GameEventBus by inject()
    lateinit var navHostController: NavHostController<ScreenContext>

    override fun create() {
        SkinID.entries.forEach { manager.load(it.path, Skin::class.java) }
        manager.load(Textures.FLOOR.path, Texture::class.java)
        manager.finishLoading()

        navHostController = NavHostController(
            game = this,
            processorsMap = mapOf(
                MenuScreen::class.java to MenuScreen(game = this),
                GameScreen::class.java to GameScreen(game = this)
            )
        )

        navHostController.navigate(MenuScreen::class.java)
    }

    override fun dispose() {
        eventBus.close()
    }


}