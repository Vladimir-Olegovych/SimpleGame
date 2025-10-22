package app

import app.navigation.Navigation
import app.screens.GameFragment
import app.screens.MainFragment
import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import di.components.AppComponent
import di.components.DaggerAppComponent
import event.GamePacket
import core.models.ClientPreference
import core.textures.SkinID
import tools.graphics.screens.fragment.Fragment
import tools.graphics.screens.navigation.NavHostController
import tools.graphics.screens.navigation.NavigationListener
import tools.kyro.client.GameClient
import tools.preference.JsonPreference
import core.values.GameValues
import javax.inject.Inject

class GameApplication: Game() {
    private val jsonPreference = JsonPreference("client", ClientPreference())
    private lateinit var appComponent: AppComponent

    @Inject lateinit var spriteBatch: SpriteBatch
    @Inject lateinit var stage: Stage
    @Inject lateinit var assetManager: AssetManager
    @Inject lateinit var gameClient: GameClient<GamePacket>

    override fun create() {
        GameValues.setClientPreference(jsonPreference.getPreference())
        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)

        SkinID.entries.forEach { assetManager.load(it.skin, Skin::class.java) }
        //assetManager.load(Textures.FLOOR.path, Texture::class.java)

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
                    navigation = it,
                    onDisconnect = {
                        navigate(Navigation.Main)
                    }
                )
            }

            navigate(Navigation.Main)
        }
    }

    override fun dispose() {
        jsonPreference.setPreference(GameValues.getClientPreference())

        gameClient.stop()
        spriteBatch.dispose()
        stage.dispose()
        assetManager.dispose()
    }

}