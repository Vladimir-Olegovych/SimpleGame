package org.example

fun main() {
    val serverApplication = ServerApplication(5000)
    serverApplication.start()
    var process = true

    while (process) {
        val command = readlnOrNull()
        when(command) {
            "stop" -> process = false
        }
    }

    serverApplication.stop()
}