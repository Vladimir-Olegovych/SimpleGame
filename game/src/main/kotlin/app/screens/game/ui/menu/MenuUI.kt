package app.screens.game.ui.menu

import app.screens.game.ui.dialog.MenuDialog
import com.artemis.annotations.Wire
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import core.ui.UIComponent
import models.textures.SkinID
import tools.graphics.screens.dialogs.DialogManager
import tools.graphics.setOnClickListener

class MenuUI(): UIComponent() {

    @Wire
    private lateinit var menuDialog: MenuDialog
    @Wire
    private lateinit var dialogManager: DialogManager
    @Wire
    private lateinit var assetManager: AssetManager

    override fun initialize(){
        val skinButton = assetManager.get<Skin>(SkinID.BUTTON.skin)

        val menu = ImageButton(skinButton, "menu").setOnClickListener {
            if (menuDialog.isShowed()) return@setOnClickListener
            menuDialog.show(dialogManager)
        }

        getTable().add(menu).height(50f).width(50f).pad(10f)
            .top()
            .right()
            .expandX()
            .expandY()
            .row()
    }

}