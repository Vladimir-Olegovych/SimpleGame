package org.example.app.ecs.components

import app.ecs.components.sender.UpdatableData
import com.artemis.Component
import models.stats.StatContainer

class StatsComponent: Component() {

    private val stats = HashMap<String, Any>()
    private val _tempStats = HashMap<String, Any>()

    fun setStats(stats: Map<String, Any>){
        this@StatsComponent.stats.clear()
        this@StatsComponent.stats.putAll(stats)
    }

    fun getMapStats(): Map<String, Any> = stats

    fun setStat(key: String, value: Any) {
        stats[key] = value
    }

    fun <T> getStat(key: String): T? {
        return stats[key] as? T
    }

    val statsUpdater = object : UpdatableData<Array<StatContainer>> {
        override fun hasUpdate(): Boolean {
            stats.forEach { (key, value) ->
                if (value != _tempStats[key]) {
                    return true
                }
            }
            return false
        }

        override fun markAsUpdated() {
            _tempStats.clear()
            _tempStats.putAll(stats)
        }

        override fun getUpdate(): Array<StatContainer> {
            val differences = ArrayList<StatContainer>()

            stats.forEach { (key, value) ->
                if (value != _tempStats[key]) {
                    differences.add(StatContainer(key, value))
                }
            }

            return differences.toTypedArray()
        }

        override fun getAll(): Array<StatContainer> {
            return stats.map { (key, value) ->
                StatContainer(key, value)
            }.toTypedArray()
        }
    }

}