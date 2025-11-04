package ecs.components

import com.artemis.Component
import models.StatContainer
import java.util.HashMap

class EntityStats: Component() {
    private val stats = HashMap<String, Any>()

    fun setAllStats(newStats: Array<StatContainer>){
        newStats.forEach { container ->
            stats[container.name] = container.value
        }
    }

    fun setStat(key: String, value: Any) {
        stats[key] = value
    }

    fun <T> getStat(key: String): T? {
        return stats[key] as? T
    }

}