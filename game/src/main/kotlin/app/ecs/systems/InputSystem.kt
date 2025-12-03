package app.ecs.systems

import app.ecs.models.SendEvents
import app.ecs.processors.HotKeysInputProcessor
import app.ecs.processors.LookInputProcessor
import app.ecs.processors.MovementInputProcessor
import app.screens.game.dialog.MenuDialog
import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.InputMultiplexer
import tools.graphics.screens.dialogs.DialogManager

class InputSystem: BaseSystem() {

    @Wire
    private lateinit var inputMultiplexer: InputMultiplexer
    @Wire
    private lateinit var dialogManager: DialogManager
    @Wire
    private lateinit var menuDialog: MenuDialog
    @Wire
    private lateinit var sendEvents: SendEvents

    private lateinit var movementInputProcessor: MovementInputProcessor
    private lateinit var lookInputProcessor: LookInputProcessor
    private lateinit var hotKeysInputProcessor: HotKeysInputProcessor

    override fun initialize() {
        movementInputProcessor = MovementInputProcessor(sendEvents)
        lookInputProcessor = LookInputProcessor(sendEvents)
        hotKeysInputProcessor = HotKeysInputProcessor(sendEvents, menuDialog, dialogManager)

        inputMultiplexer.addProcessor(hotKeysInputProcessor)
        inputMultiplexer.addProcessor(movementInputProcessor)
        inputMultiplexer.addProcessor(lookInputProcessor)
    }

    override fun processSystem() {
        val enabled = !menuDialog.isShowed()
        movementInputProcessor.setEnabled(enabled)
        lookInputProcessor.setEnabled(enabled)
        hotKeysInputProcessor.setEnabled(enabled)
    }
}