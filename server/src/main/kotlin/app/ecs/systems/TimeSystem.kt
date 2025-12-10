package app.ecs.systems

import app.ecs.components.models.ServerTime
import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import org.example.core.models.settings.ServerPreference

class TimeSystem: BaseSystem() {

    @Wire
    private lateinit var serverTime: ServerTime
    @Wire
    private lateinit var serverPreference: ServerPreference

    private var maxTime = 0F

    override fun initialize() {
        maxTime = serverPreference.dayTime + serverPreference.eveningTime +
                  serverPreference.nightTime + serverPreference.nightTime
    }

    override fun processSystem() {
        serverTime.time += world.delta * serverPreference.timeScale
        if (serverTime.time > maxTime) serverTime.time = 0F
    }
}