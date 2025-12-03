package org.example.app.ecs.components

import com.artemis.Component
import models.stats.StatContainer
import org.example.app.ecs.components.sender.CSender

class StatsComponent: CSender<Array<StatContainer>>, Component() {

    private val stats = HashMap<String, Any>()
    private val _tempStats = HashMap<String, Any>()

    fun setAllStats(newStats: Map<String, Any>){
        stats.clear()
        stats.putAll(newStats)
    }

    fun getMapStats(): Map<String, Any> = stats

    fun getArrayStatContainer(): Array<StatContainer> {
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

    override fun fetchSendData(): Array<StatContainer> {
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