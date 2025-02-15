package tools.artemis.world

import com.artemis.BaseSystem
import com.artemis.World
import com.artemis.WorldConfiguration

class ArtemisWorldBuilder private constructor(
    val systems: Array<BaseSystem>?,
    val registeredObjects: Array<Any>?,
) {
    class Builder {
        private var systems: Array<BaseSystem>? = null
        private var registeredObjects: Array<Any>? = null

        fun setRegisteredObjectsArray(registeredObjects: Array<Any>) = apply {
            this.registeredObjects = registeredObjects
        }

        fun setSystemArray(systems: Array<BaseSystem>) = apply {
            this.systems = systems
        }

        fun build(): World {
            val configuration = WorldConfiguration()
            registeredObjects?.forEach { registeredObject ->
                configuration.register(registeredObject)
            }
            systems?.forEach { system ->
                configuration.setSystem(system)
            }
            return World(configuration)
        }
    }

}