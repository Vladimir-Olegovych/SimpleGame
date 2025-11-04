package org.example.ecs.components

import com.artemis.Component
import models.StatContainer
import java.util.HashMap

class EntityStats: Component() {

    private val stats = HashMap<String, Any>()
    private val _tempStats = HashMap<String, Any>()

    fun setAllStats(newStats: Map<String, Any>){
        stats.clear()
        stats.putAll(newStats)
    }

    fun getMapStats(): Map<String, Any> = stats

    fun getArrayStats(): Array<StatContainer> {
        return stats.map { (key, value) ->
            StatContainer(key, value)
        }.toTypedArray()
    }

    fun setStat(key: String, value: Any) {
        stats[key] = value
    }

    fun <T> getStat(key: String): T? {
        return stats[key] as? T
    }

    fun acceptUpdate(): Array<StatContainer> {
        val differences = ArrayList<StatContainer>()

        stats.forEach { (key, value) ->
            if (value != _tempStats[key]) {
                differences.add(StatContainer(key, value))
            }
        }

        if(differences.isNotEmpty()) {
            _tempStats.clear()
            _tempStats.putAll(stats)
        }

        return differences.toTypedArray()
    }

}