package app.ecs.systems

import app.di.modules.UiViewport
import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import core.textures.SkinID
import tools.graphics.setOnClickListener

class UiSystem(private val onDisconnect: () -> Unit): BaseSystem() {
    @Wire
    private lateinit var stage: Stage
    @Wire
    private lateinit var uiViewport: UiViewport
    @Wire
    private lateinit var assetManager: AssetManager

    override fun initialize() {
        val skinButton = assetManager.get<Skin>(SkinID.BUTTON.skin)

        val gameTable = Table().apply {
            setFillParent(true)
        }

        val menu = ImageButton(skinButton, "menu").setOnClickListener {
            onDisconnect.invoke()
        }

        gameTable.add(menu).height(70f).width(70f).pad(10f)
            .top()
            .right()
            .expandX()
            .expandY()
            .row()

        val inventoryTable = Table()

        for (i in 0 .. 8) {
            val image = ImageButton(skinButton, "menu")
            inventoryTable.add(image).height(50f).width(50f)
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