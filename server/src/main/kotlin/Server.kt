package org.example

import org.example.values.Commands

fun main() {
    val serverApplication = ServerApplication(5000)
    serverApplication.start()
    var process = true

    while (process) {
        val command = readlnOrNull()
        when(command) {
            Commands.STOP -> process = false
            Commands.TIK -> println(serverApplication.getTick())
        }
    }

    serverApplication.stop()
}