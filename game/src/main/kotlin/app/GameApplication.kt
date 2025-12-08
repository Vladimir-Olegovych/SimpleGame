package app

import app.di.appModule
import app.navigation.Navigation
import app.screens.MainFragment
import app.screens.StructureEditorFragment
import app.screens.game.screen.GameFragment
import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import core.models.settings.ClientPreference
import event.GamePacket
import models.textures.SkinID
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import tools.graphics.screens.navigation.NavHostController
import tools.kyro.client.GameClient
import tools.preference.JsonPreference

class GameApplication: KoinComponent, Game() {

    private val jsonPreference: JsonPreference<ClientPreference> by inject()
    private val clientPreference: ClientPreference by inject()
    private val spriteBatch: SpriteBatch by inject()
    private val stage: Stage by inject()
    private val assetManager: AssetManager by inject()
    private val gameClient: GameClient<GamePacket> by inject()

    override fun create() {
        startKoin {
            modules(appModule)
        }

        SkinID.entries.forEach {
            assetManager.load(it.skin, Skin::class.java)
        }
        //assetManager.load(Textures.FLOOR.path, Texture::class.java)

        assetManager.finishLoading()

        val navHostController = NavHostController<Navigation>(this)

        navHostController.apply {
            fragment<Navigation.Main> {
                return@fragment MainFragment(
                    navigation = it,
                    onStartGame = {
                        navigate(Navigation.Game())
                    },
                    onEditor = {
                        navigate(Navigation.StructureEditor)
                    }
                )
            }

            fragment<Navigation.Game> {
                return@fragment GameFragment(
                    navigation = it,
                    onBack = {
                        navigate(Navigation.Main)
                    }
                )
            }

            fragment<Navigation.StructureEditor> {
                return@fragment StructureEditorFragment(
                    navigation = it,
                    onBack = {
                        navigate(Navigation.Main)
                    }
                )
            }

            navigate(Navigation.Main)
        }
    }

    override fun dispose() {
        jsonPreference.setPreference(clientPreference)

        gameClient.stop()
        spriteBatch.dispose()
        stage.dispose()
        assetManager.dispose()
    }

}