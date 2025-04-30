package app

import model.Event
import app.navigation.Navigation
import app.screens.GameFragment
import app.screens.MainFragment
import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import di.components.AppComponent
import di.components.DaggerAppComponent
import tools.graphics.screens.fragment.Fragment
import tools.graphics.screens.navigation.NavHostController
import tools.graphics.screens.navigation.NavigationListener
import tools.graphics.textures.SkinID
import tools.graphics.textures.Textures
import tools.kyro.client.GameClient
import javax.inject.Inject

class GameApplication: Game() {
    private lateinit var appComponent: AppComponent

    @Inject lateinit var spriteBatch: SpriteBatch
    @Inject lateinit var stage: Stage
    @Inject lateinit var assetManager: AssetManager
    @Inject lateinit var gameClient: GameClient<Event>

    override fun create() {
        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)

        SkinID.entries.forEach { assetManager.load(it.path, Skin::class.java) }
        assetManager.load(Textures.FLOOR.path, Texture::class.java)
        assetManager.finishLoading()

        val navHostController = NavHostController<Navigation>(this)

        navHostController.setOnNavigationListener(object : NavigationListener<Navigation> {
            override fun onNavigationSuccess(destination: Navigation, fragment: Fragment) {
                when(destination) {
                    is Navigation.Main -> appComponent.inject(fragment as MainFragment)
                    is Navigation.Game -> appComponent.inject(fragment as GameFragment)
                }
            }
        })
        navHostController.apply {
            fragment<Navigation.Main> {
                return@fragment MainFragment(
                    navigation = it,
                    onStart = {
                        navigate(Navigation.Game)
                    }
                )
            }
            fragment<Navigation.Game> {
                return@fragment GameFragment(
                    navigation = it
                )
            }

            navigate(Navigation.Main(label = "World of Fort Ships"))
        }
    }

    override fun dispose() {
        gameClient.stop()
        spriteBatch.dispose()
        stage.dispose()
        assetManager.dispose()
    }

}