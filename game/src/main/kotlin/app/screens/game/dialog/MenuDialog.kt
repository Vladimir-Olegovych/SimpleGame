package app.screens.game.dialog

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import core.textures.SkinID
import tools.graphics.screens.dialogs.Dialog
import tools.graphics.setOnClickListener

class MenuDialog(
    private val stage: Stage,
    private val assetManager: AssetManager,
    private val onDisconnect: () -> Unit
): Dialog() {

    private val texture = assetManager.get<TextureAtlas>(SkinID.BLOCK.atlas).findRegion("ic_stone_block")
    private val skin = assetManager.get<Skin>(SkinID.BUTTON.skin)

    private val fullscreenOverlay = Table().apply {
        setFillParent(true)
        //background(TextureRegionDrawable(texture))
        add(
            TextButton("quit", this@MenuDialog.skin).setOnClickListener {
                onDisconnect.invoke()
            }
        ).height(40F).width(70F).padTop(8F).row()
    }

    override fun onCreate() {
        stage.addActor(fullscreenOverlay)
    }

    override fun onDestroy() {
        fullscreenOverlay.remove()
    }
}