package org.example

import org.example.values.Commands

fun main() {
    val serverApplication = ServerApplication(port = 5000)
    serverApplication.start()
    var process = true
    while (process) {
        val command = readlnOrNull()
        when(command) {
            Commands.STOP -> process = false
        }
    }

    serverApplication.stop()
}