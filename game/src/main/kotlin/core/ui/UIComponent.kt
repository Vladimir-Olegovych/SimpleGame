package core.ui

import com.artemis.World
import com.badlogic.gdx.scenes.scene2d.ui.Table

abstract class UIComponent {

    private var table: Table? = null
    private var world: World? = null

    fun getTable(): Table = table!!
    fun getWorld(): World = world!!

    fun init(world: World, table: Table) {
        this.table = table
        this.world = world
        world.inject(this)
        initialize()
    }

    protected abstract fun initialize()
    open fun render(deltaTime: Float) {}

}