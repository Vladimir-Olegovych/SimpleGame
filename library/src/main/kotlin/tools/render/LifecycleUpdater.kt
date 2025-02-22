package tools.render

abstract class LifecycleUpdater(
    private var deltaTime: Float = DELTA_TIME
): Runnable {

    private var step = (deltaTime * 1_000_000_000L).toLong()
    private var lastTime = System.nanoTime()
    private var timeSinceLastUpdate = 0L
    private var isRunning = true

    private fun update() {
        lastTime = System.nanoTime().also { timeSinceLastUpdate += it - lastTime }
        if (timeSinceLastUpdate < step) {
            val remain = step - timeSinceLastUpdate
            if (remain >= 1_000_000L) Thread.sleep(remain / 1_000_000L)
        } else {
            do {
                update(deltaTime)
                timeSinceLastUpdate -= step
            } while (timeSinceLastUpdate >= step)
        }
    }

    abstract fun update(deltaTime: Float)
    abstract fun dispose()

    fun stop(){
        if (isRunning) isRunning = false
    }

    override fun run() {
        lastTime = System.nanoTime()
        while (isRunning) update()
        isRunning = true
        dispose()
    }


    companion object {
        const val DELTA_TIME = 1F / 60F
    }
}