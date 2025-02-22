package tools.screens.navigation

import com.badlogic.gdx.Game
import tools.screens.screen.ScreenContext

class NavHostController<T : ScreenContext>(
    private val game: Game,
    private val processorsMap: Map<Class<out T>, T>
) {
    private var currentProcessor: T? = null

    fun navigate(destination: Class<out T>) {
        val processor = processorsMap[destination]?: return
        if (currentProcessor == processor) return

        currentProcessor?.onDetached()
        currentProcessor = processor
        game.screen = processor.screen
        processor.onAttached()
    }

}