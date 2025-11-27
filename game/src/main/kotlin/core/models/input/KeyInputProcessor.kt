package core.models.input

import com.badlogic.gdx.InputProcessor

open class KeyInputProcessor: InputProcessor {
    override fun keyDown(keycode: Int): Boolean = false

    override fun keyUp(keycode: Int): Boolean = false

    override fun keyTyped(char: Char): Boolean = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun touchCancelled(p0: Int, p1: Int, p2: Int, p3: Int): Boolean = false

    override fun touchDragged(p0: Int, p1: Int, p2: Int): Boolean = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false

    override fun scrolled(p0: Float, p1: Float): Boolean = false
}