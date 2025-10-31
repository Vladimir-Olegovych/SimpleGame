package ecs.systems

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
    private lateinit var assetManager: AssetManager

    override fun initialize() {
        val skin = assetManager.get<Skin>(SkinID.BUTTON.skin)

        val gameTable = Table().apply {
            setFillParent(true)
            top()
        }

        val play = ImageButton(skin, "play").setOnClickListener {
            onDisconnect.invoke()
        }
        gameTable.add(play).height(2F).width(5F).row()
        stage.addActor(gameTable)
    }


    override fun processSystem() {
        stage.act(world.delta)
        stage.draw()
    }

}