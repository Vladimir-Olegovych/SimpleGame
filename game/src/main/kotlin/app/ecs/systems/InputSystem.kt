package app.ecs.systems

import app.ecs.processors.HotKeysInputProcessor
import app.ecs.processors.LookInputProcessor
import app.ecs.processors.MovementInputProcessor
import app.screens.game.ui.dialog.MenuDialog
import com.artemis.BaseSystem
import com.artemis.annotations.Wire

class InputSystem: BaseSystem() {

    @Wire
    private lateinit var menuDialog: MenuDialog
    @Wire
    private lateinit var movementInputProcessor: MovementInputProcessor
    @Wire
    private lateinit var lookInputProcessor: LookInputProcessor
    @Wire
    private lateinit var hotKeysInputProcessor: HotKeysInputProcessor


    override fun processSystem() {
        val enabled = !menuDialog.isShowed()
        movementInputProcessor.setEnabled(enabled)
        lookInputProcessor.setEnabled(enabled)
        hotKeysInputProcessor.setEnabled(enabled)
    }
}