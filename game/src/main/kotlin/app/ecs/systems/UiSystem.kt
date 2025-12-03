package app.ecs.systems

import app.di.UiViewport
import app.screens.game.dialog.MenuDialog
import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import core.textures.SkinID
import tools.graphics.screens.dialogs.DialogManager
import tools.graphics.setOnClickListener

class UiSystem(): BaseSystem() {
    @Wire
    private lateinit var menuDialog: MenuDialog
    @Wire
    private lateinit var dialogManager: DialogManager
    @Wire
    private lateinit var stage: Stage
    @Wire
    private lateinit var inputMultiplexer: InputMultiplexer
    @Wire
    private lateinit var uiViewport: UiViewport
    @Wire
    private lateinit var assetManager: AssetManager

    override fun initialize() {
        inputMultiplexer.addProcessor(stage)

        val skinButton = assetManager.get<Skin>(SkinID.BUTTON.skin)

        val gameTable = Table().apply {
            setFillParent(true)
        }

        val menu = ImageButton(skinButton, "menu").setOnClickListener {
            if (menuDialog.isShowed()) return@setOnClickListener
            menuDialog.show(dialogManager)
        }

        gameTable.add(menu).height(50f).width(50f).pad(10f)
            .top()
            .right()
            .expandX()
            .expandY()
            .row()

        val inventoryTable = Table()

        for (i in 0 .. 8) {
            val image = ImageButton(skinButton, "menu")
            inventoryTable.add(image).height(40f).width(40f)
        }
        gameTable.add(inventoryTable).padBottom(10F)
            .bottom()
            .expandY()

        stage.addActor(gameTable)
    }

    override fun begin() {
        uiViewport.apply()
    }

    override fun processSystem() {
        stage.act(world.delta)
        stage.draw()
    }

}