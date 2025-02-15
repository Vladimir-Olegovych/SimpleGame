package ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import tools.setOnChange
import tools.textures.SkinID

@All
class UiSystem(private val toMenu: () -> Unit): BaseSystem() {

    @Wire private lateinit var stage: Stage
    @Wire private lateinit var manager: AssetManager

    override fun initialize() {
        val skin = manager.get<Skin>(SkinID.MAIN.path)

        val uiTable = Table().apply {
            setFillParent(true)
            padTop(32F)
            top()
        }

        val play = ImageButton(skin, "play").setOnChange {
            toMenu.invoke()
        }

        uiTable.add(Label("Game", skin)).row()
        uiTable.add(play)

        stage.addActor(uiTable)
    }

    override fun processSystem() {
        stage.act(world.delta)
    }

    override fun dispose() {
        stage.clear()
    }

}