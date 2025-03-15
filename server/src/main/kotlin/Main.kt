import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun main() {
    val scope = CoroutineScope(Dispatchers.IO)

    val serverGame = ServerGame()

    scope.launch { serverGame.run() }
    readlnOrNull()
    scope.launch { serverGame.stop() }
}