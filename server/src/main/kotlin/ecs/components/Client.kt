package org.example.ecs.components

import com.artemis.Component
import com.esotericsoftware.kryonet.Connection

class Client: Component() {
    var connection: Connection? = null
    var events: Array<Any>? = null
    var isNewConnection = true
}