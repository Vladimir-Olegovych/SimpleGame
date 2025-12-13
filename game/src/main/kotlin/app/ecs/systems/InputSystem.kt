package app.ecs.systems

import app.processors.HotKeysInputProcessor
import app.processors.LookInputProcessor
import app.processors.MovementInputProcessor
import app.screens.game.ui.dialog.MenuDialog
import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.InputProcessor
import tools.graphics.input.GameInputProcessor

class InputSystem(private val onQuit: () -> Unit): InputProcessor, BaseSystem() {

    @Wire
    private lateinit var menuDialog: MenuDialog

    private lateinit var inputProcessors: Array<GameInputProcessor>


    override fun initialize() {
        inputProcessors = arrayOf(
            MovementInputProcessor(),
            LookInputProcessor(),
            HotKeysInputProcessor()
        )
        inputProcessors.forEach { world.inject(it) }

        menuDialog.menuListeners.add(object : MenuDialog.Listener {
            override fun onCreate() {
                inputProcessors.forEach { it.onPause() }
            }

            override fun onDestroy() {
                inputProcessors.forEach { it.onResume() }
            }

            override fun onQuit() {
                onQuit.invoke()
            }
        })
    }

    override fun processSystem() {}

    override fun keyDown(keycode: Int): Boolean {
        inputProcessors.forEach { it.keyDown(keycode) }
        return false
    }
    override fun keyUp(keycode: Int): Boolean {
        inputProcessors.forEach { it.keyUp(keycode) }
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        inputProcessors.forEach { it.keyTyped(character) }
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        inputProcessors.forEach { it.touchDown(screenX, screenY, pointer, button) }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        inputProcessors.forEach { it.touchUp(screenX, screenY, pointer, button) }
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        inputProcessors.forEach { it.touchCancelled(screenX, screenY, pointer, button) }
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        inputProcessors.forEach { it.touchDragged(screenX, screenY, pointer) }
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        inputProcessors.forEach { it.mouseMoved(screenX, screenY) }
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        inputProcessors.forEach { it.scrolled(amountX, amountY) }
        return false
    }

}