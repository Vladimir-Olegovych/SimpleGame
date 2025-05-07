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
            Commands.GRAVITY -> {
                try {
                    val cords = command.removePrefix(Commands.GRAVITY).trim().split(",")
                    val x = cords[0].trim().toFloat()
                    val y = cords[1].trim().toFloat()
                    serverApplication.setGravity(x, y)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    serverApplication.stop()
}