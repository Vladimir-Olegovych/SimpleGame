package app.screens.game.ui.dialog

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import models.textures.SkinID
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tools.graphics.drawable.ColorDrawable
import tools.graphics.screens.dialogs.Dialog
import tools.graphics.setOnClickListener

class MenuDialog: KoinComponent, Dialog() {
    val menuListeners = ArrayList<Listener>()

    private val stage: Stage by inject()
    private val assetManager: AssetManager by inject()
    private val skin = assetManager.get<Skin>(SkinID.BUTTON.skin)

    private val fullscreenOverlay = Table().apply {
        setFillParent(true)
        background(ColorDrawable(0f, 0f, 0f, 0.7f))
        add(
            TextButton("resume", this@MenuDialog.skin).setOnClickListener {
                dismiss()
                menuListeners.forEach { it.onResume() }
            }
        ).height(40F).width(70F).row()
        add(
            TextButton("settings", this@MenuDialog.skin).setOnClickListener {
                dismiss()
                menuListeners.forEach { it.onSettings() }
            }
        ).height(40F).width(70F).padTop(8F).row()
        add(
            TextButton("quit", this@MenuDialog.skin).setOnClickListener {
                dismiss()
                menuListeners.forEach { it.onQuit() }
            }
        ).height(40F).width(70F).padTop(8F).row()
    }

    override fun onCreate() {
        menuListeners.forEach { it.onCreate() }
        stage.addActor(fullscreenOverlay)
    }

    override fun onDestroy() {
        menuListeners.forEach { it.onDestroy() }
        fullscreenOverlay.remove()
    }

    interface Listener {
        fun onCreate() {}
        fun onDestroy() {}
        fun onResume() {}
        fun onSettings() {}
        fun onQuit() {}
    }
}