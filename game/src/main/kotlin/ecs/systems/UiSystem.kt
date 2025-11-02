package ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import core.textures.SkinID
import di.modules.UiViewport
import tools.graphics.setOnClickListener

class UiSystem(private val onDisconnect: () -> Unit): BaseSystem() {
    @Wire
    private lateinit var stage: Stage
    @Wire
    private lateinit var uiViewport: UiViewport
    @Wire
    private lateinit var assetManager: AssetManager

    override fun initialize() {
        val skin = assetManager.get<Skin>(SkinID.BUTTON.skin)

        val gameTable = Table().apply {
            setFillParent(true)
            top()
            right()
        }

        val menu = ImageButton(skin, "menu").setOnClickListener {
            onDisconnect.invoke()
        }

        gameTable.add(menu).height(70f).width(70f).pad(10f)

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